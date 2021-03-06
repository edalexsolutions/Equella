package com.tle.web.controls.youtube.migration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import com.tle.beans.item.Item;
import com.tle.beans.item.attachments.Attachment;
import com.tle.common.Check;
import com.tle.core.guice.Bind;
import com.tle.core.institution.convert.PostReadMigrator;
import com.tle.core.item.convert.ItemConverter.ItemConverterInfo;

/**
 * @author larry
 *
 */
@Bind
@Singleton
public class UpdateOlderYoutubeAttachmentsXmlMigration implements PostReadMigrator<ItemConverterInfo>
{
	@SuppressWarnings("unused")
	@Override
	public void migrate(ItemConverterInfo obj) throws IOException
	{
		// return value not referred to for XmlMigration, but we code it here in
		// for debugging convenience
		boolean wasModified = false;
		Item item = obj.getItem();
		List<Attachment> attachments = item.getAttachments();
		if( !Check.isEmpty(attachments) )
		{
			for( Attachment attachment : attachments )
			{
				Map<String, Object> dataMap = attachment.getDataAttributes();
				if( dataMap != null )
				{
					wasModified |= UpdateOlderYoutubeAttachmentMigration.checkForUpdate(dataMap);
				}
			}
		}
	}
}
