/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
/*
 * AttachmentExporterFactory.java
 * 
 * Created on May 29, 2011, 12:00 PM by Edwin van der Wal
 */
package games.strategy.engine.data.export;

import games.strategy.engine.data.IAttachment;

/**
 * This class returns the right AttachmentExporter based on the
 * 
 * @author Edwin van der Wal
 * 
 */
public class AttachmentExporterFactory
{
	/**
	 * 
	 * @param attachment
	 *            the attachment to base the exporter on
	 * @return an Exporter to export the attachment
	 * @throws AttachmentExportException
	 */
	public static IAttachmentExporter getExporter(final IAttachment attachment) throws AttachmentExportException
	{
		if (attachment.getClass() == games.strategy.triplea.attatchments.CanalAttachment.class)
			return new DefaultAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.RulesAttachment.class)
			return new RulesAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.TechAttachment.class)
			return new TechAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.TerritoryAttachment.class)
			return new TerritoryAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.TriggerAttachment.class)
			return new TriggerAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.UnitAttachment.class)
			return new UnitAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.UnitSupportAttachment.class)
			return new UnitSupportAttachmentExporter();
		if (attachment.getClass() == games.strategy.triplea.attatchments.PlayerAttachment.class)
			return new PlayerAttachmentExporter();
		throw new AttachmentExportException("No exportor defined for: " + attachment.getClass().getCanonicalName());
	}
}
