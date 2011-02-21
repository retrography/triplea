/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package games.strategy.engine.lobby.client.ui;

import games.strategy.engine.chat.Chat;
import games.strategy.engine.chat.ChatMessagePanel;
import games.strategy.engine.chat.ChatPlayerPanel;
import games.strategy.engine.chat.IPlayerActionFactory;
import games.strategy.engine.framework.GameRunner;
import games.strategy.engine.lobby.client.LobbyClient;
import games.strategy.engine.lobby.client.login.LobbyServerProperties;
import games.strategy.engine.lobby.server.IModeratorController;
import games.strategy.engine.lobby.server.LobbyServer;
import games.strategy.engine.lobby.server.ModeratorController;
import games.strategy.net.IMessenger;
import games.strategy.net.IMessengerErrorListener;
import games.strategy.net.INode;
import games.strategy.util.EventThreadJOptionPane;
import games.strategy.util.MD5Crypt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;

public class LobbyFrame extends JFrame
{
    
    private final LobbyClient m_client;
    
    private final ChatMessagePanel m_chatMessagePanel;
    
    
    public LobbyFrame(LobbyClient client, LobbyServerProperties props)
    {
        super("TripleA Lobby");
        setIconImage(GameRunner.getGameIcon(this));
        m_client = client;
        setJMenuBar(new LobbyMenu(this));
        
        Chat chat = new Chat(m_client.getMessenger(), LobbyServer.LOBBY_CHAT, m_client.getChannelMessenger(), m_client.getRemoteMessenger());
        
        m_chatMessagePanel = new ChatMessagePanel(chat);
        showServerMessage(props);
        
        m_chatMessagePanel.setShowTime(true);
        
        ChatPlayerPanel chatPlayers = new ChatPlayerPanel(null);
        chatPlayers.addHiddenPlayerName(LobbyServer.ADMIN_USERNAME);
        chatPlayers.setChat(chat);
        chatPlayers.setPreferredSize(new Dimension(200,600 ));
        chatPlayers.addActionFactory(new IPlayerActionFactory()
        {
        
            public List<Action> mouseOnPlayer(INode clickedOn)
            {
                return createAdminActions(clickedOn);
            }
        
        });
    
        LobbyGamePanel gamePanel = new LobbyGamePanel(m_client.getMessengers());
        
    
        JSplitPane leftSplit = new JSplitPane( );
        leftSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
        leftSplit.setTopComponent(gamePanel);
        leftSplit.setBottomComponent(m_chatMessagePanel);
        
        leftSplit.setResizeWeight(0.8);
        gamePanel.setPreferredSize(new Dimension(700,200 ));
        m_chatMessagePanel.setPreferredSize(new Dimension(700,400 ));
        
        
        JSplitPane mainSplit = new JSplitPane();
        mainSplit.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setLeftComponent(leftSplit);
        mainSplit.setRightComponent(chatPlayers);
        
        add(mainSplit, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        
      
        
        m_client.getMessenger().addErrorListener(new IMessengerErrorListener()
        {
        
            public void messengerInvalid(IMessenger messenger, Exception reason)
            {
                connectionToServerLost();
        
            }
        
        });
        
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                shutdown();
            }
        });
        
        
    }

    private void showServerMessage(LobbyServerProperties props)
    {
        if(props.getServerMessage() != null && props.getServerMessage().length() > 0) 
        {
            m_chatMessagePanel.addServerMessage(props.getServerMessage());
        }
    }
    
    private List<Action> createAdminActions(final INode clickedOn)
    {
        if(!m_client.isAdmin())
            return Collections.emptyList();

        if(clickedOn.equals(m_client.getMessenger().getLocalNode()))
            return Collections.emptyList();

        final IModeratorController controller = (IModeratorController) m_client.getRemoteMessenger().getRemote(ModeratorController.getModeratorControllerName());
        List<Action> rVal = new ArrayList<Action>();
        rVal.add(new AbstractAction("Boot " + clickedOn.getName())
        {

            public void actionPerformed(ActionEvent e)
            {
                if(!confirm("Boot " + clickedOn.getName()))
                {
                    return;
                }

                controller.boot(clickedOn);
            }

        });

        rVal.add(new AbstractAction("Ban IP address")
        {
            public void actionPerformed(ActionEvent e)
            {
                List<String> timeUnits = new ArrayList<String>();
                timeUnits.add("Minute");
                timeUnits.add("Hour");
                timeUnits.add("Day");
                timeUnits.add("Week");
                timeUnits.add("Month");
                timeUnits.add("Year");
                timeUnits.add("Forever");

                int result = JOptionPane.showOptionDialog(LobbyFrame.this,"Please select the timespan unit of measurement: ", "Select Timespan Unit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null, timeUnits.toArray(),timeUnits.toArray()[3]);

                if(result < 0)
                    return;

                String selectedTimeUnit = (String)timeUnits.toArray()[result];

                if(selectedTimeUnit.equals("Forever"))
                {
                    controller.banIp(clickedOn, null);
                    return;
                }

                String stringr = JOptionPane.showInputDialog(LobbyFrame.this, "Please enter the amount of time to ban the player: (In " + selectedTimeUnit + "s)", 1);

                if(stringr == null)
                    return;

                long result2 = Long.parseLong(stringr);
                if(result2 < 0)
                    return;

                long ticks = 0;

                if(selectedTimeUnit.equals("Minute"))
                    ticks = result2 * 1000 * 60;
                else if(selectedTimeUnit.equals("Hour"))
                    ticks = result2 * 1000 * 60 * 60;
                else if(selectedTimeUnit.equals("Day"))
                    ticks = result2 * 1000 * 60 * 60 * 24;
                else if(selectedTimeUnit.equals("Week"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 7;
                else if(selectedTimeUnit.equals("Month"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 30;
                else if(selectedTimeUnit.equals("Year"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 365;

                long expire = System.currentTimeMillis() +
                              ticks;

                controller.banIp(clickedOn, new Date(expire));
            }
        });

        rVal.add(new AbstractAction("Ban Mac address")
        {
            public void actionPerformed(ActionEvent e)
            {
                List<String> timeUnits = new ArrayList<String>();
                timeUnits.add("Minute");
                timeUnits.add("Hour");
                timeUnits.add("Day");
                timeUnits.add("Week");
                timeUnits.add("Month");
                timeUnits.add("Year");
                timeUnits.add("Forever");

                int result = JOptionPane.showOptionDialog(LobbyFrame.this,"Please select the timespan unit of measurement: ", "Select Timespan Unit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null, timeUnits.toArray(),timeUnits.toArray()[3]);

                if(result < 0)
                    return;

                String selectedTimeUnit = (String)timeUnits.toArray()[result];

                if(selectedTimeUnit.equals("Forever"))
                {
                    controller.banMac(clickedOn, null);
                    return;
                }

                String stringr = JOptionPane.showInputDialog(LobbyFrame.this, "Please enter the amount of time to ban the player: (In " + selectedTimeUnit + "s)", 1);

                if(stringr == null)
                    return;

                long result2 = Long.parseLong(stringr);
                if(result2 < 0)
                    return;

                long ticks = 0;

                if(selectedTimeUnit.equals("Minute"))
                    ticks = result2 * 1000 * 60;
                else if(selectedTimeUnit.equals("Hour"))
                    ticks = result2 * 1000 * 60 * 60;
                else if(selectedTimeUnit.equals("Day"))
                    ticks = result2 * 1000 * 60 * 60 * 24;
                else if(selectedTimeUnit.equals("Week"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 7;
                else if(selectedTimeUnit.equals("Month"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 30;
                else if(selectedTimeUnit.equals("Year"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 365;

                long expire = System.currentTimeMillis() +
                              ticks;

                controller.banMac(clickedOn, new Date(expire));
            }
        });

        rVal.add(new AbstractAction("Ban IP and Mac addresses")
        {
            public void actionPerformed(ActionEvent e)
            {
                List<String> timeUnits = new ArrayList<String>();
                timeUnits.add("Minute");
                timeUnits.add("Hour");
                timeUnits.add("Day");
                timeUnits.add("Week");
                timeUnits.add("Month");
                timeUnits.add("Year");
                timeUnits.add("Forever");

                int result = JOptionPane.showOptionDialog(LobbyFrame.this,"Please select the timespan unit of measurement: ", "Select Timespan Unit", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,null, timeUnits.toArray(),timeUnits.toArray()[3]);

                if(result < 0)
                    return;

                String selectedTimeUnit = (String)timeUnits.toArray()[result];

                if(selectedTimeUnit.equals("Forever"))
                {
                    controller.banIp(clickedOn, null);
                    controller.banMac(clickedOn, null);
                    return;
                }

                String stringr = JOptionPane.showInputDialog(LobbyFrame.this, "Please enter the amount of time to ban the player: (In " + selectedTimeUnit + "s)", 1);

                if(stringr == null)
                    return;

                long result2 = Long.parseLong(stringr);
                if(result2 < 0)
                    return;

                long ticks = 0;

                if(selectedTimeUnit.equals("Minute"))
                    ticks = result2 * 1000 * 60;
                else if(selectedTimeUnit.equals("Hour"))
                    ticks = result2 * 1000 * 60 * 60;
                else if(selectedTimeUnit.equals("Day"))
                    ticks = result2 * 1000 * 60 * 60 * 24;
                else if(selectedTimeUnit.equals("Week"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 7;
                else if(selectedTimeUnit.equals("Month"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 30;
                else if(selectedTimeUnit.equals("Year"))
                    ticks = result2 * 1000 * 60 * 60 * 24 * 365;

                long expire = System.currentTimeMillis() +
                              ticks;

                controller.banIp(clickedOn, new Date(expire));
                controller.banMac(clickedOn, new Date(expire));
            }
        });

        rVal.add(new AbstractAction("Mute IP and Mac addresses")
        {
            public void actionPerformed(ActionEvent e)
            {
                JLabel label = new JLabel("How many minutes should this player be muted?");
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(10, 0, 60 * 24 * 7, 1));
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
                panel.add(label);
                panel.add(spinner);
                if (JOptionPane.showConfirmDialog(LobbyFrame.this, panel, "Mute Player", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
                {
                    Object value = spinner.getValue();

                    if (value == null)
                        return;

                    long result2 = Long.parseLong(value.toString());
                    if (result2 < 0)
                    {
                        return;
                    }

                    long ticks = result2 * 1000 * 60;

                    long expire = System.currentTimeMillis()
                            + ticks;

                    controller.muteIp(clickedOn, new Date(expire));
                    controller.muteMac(clickedOn, new Date(expire));
                }
            }
        });

        rVal.add(new AbstractAction("Show player information")
        {
            public void actionPerformed(ActionEvent e)
            {
                String text = controller.getInformationOn(clickedOn);
                JTextPane textPane = new JTextPane();
                textPane.setEditable(false);
                textPane.setText(text);
                JOptionPane.showMessageDialog(null, textPane, "Player Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        rVal.add(new AbstractAction("Reset password")
        {
            public void actionPerformed(ActionEvent e)
            {
                String newPassword = JOptionPane.showInputDialog(JOptionPane.getFrameForComponent(LobbyFrame.this), "Enter new password");
                if(newPassword == null || newPassword.length() < 2)
                    return;

                String error = controller.setPassword(clickedOn,MD5Crypt.crypt(newPassword));
                String msg = error == null ? "Password set" : error;

                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(LobbyFrame.this), msg);
            }
        });
        return rVal;
    }
    
    private boolean confirm(String question) 
    {
        int rVal = JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(this), question, "Question", JOptionPane.OK_CANCEL_OPTION);
        return rVal == JOptionPane.OK_OPTION;
    }

    public LobbyClient getLobbyClient()
    {
        return m_client;
    }
    
    void setShowChatTime(boolean showTime)
    {
        if (m_chatMessagePanel != null)
            m_chatMessagePanel.setShowTime(showTime);
    }
    
    
    void shutdown()
    {
        System.exit(0);
    }

    private void connectionToServerLost()
    {
        EventThreadJOptionPane.showMessageDialog(LobbyFrame.this, "Connection to Server Lost", "Connection Lost", JOptionPane.ERROR_MESSAGE, null);
    }

}