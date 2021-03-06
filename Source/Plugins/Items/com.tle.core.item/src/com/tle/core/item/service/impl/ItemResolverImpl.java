package com.tle.core.item.service.impl;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.dytech.devlib.PropBagEx;
import com.tle.annotation.NonNullByDefault;
import com.tle.annotation.Nullable;
import com.tle.beans.item.IItem;
import com.tle.beans.item.ItemKey;
import com.tle.beans.item.attachments.IAttachment;
import com.tle.common.beans.exception.NotFoundException;
import com.tle.core.guice.Bind;
import com.tle.core.item.service.ItemResolver;
import com.tle.core.item.service.ItemResolverExtension;
import com.tle.core.plugins.PluginTracker;

/**
 * @author Aaron
 */
@SuppressWarnings("nls")
@NonNullByDefault
@Bind(ItemResolver.class)
@Singleton
public class ItemResolverImpl implements ItemResolver
{
	@Inject
	private PluginTracker<ItemResolverExtension> resolverTracker;

	@Nullable
	@Override
	public <I extends IItem<?>> I getItem(ItemKey itemId, @Nullable String extensionType)
	{
		return getResolver(extensionType).resolveItem(itemId);
	}

	@Override
	public PropBagEx getXml(IItem<?> item, @Nullable String extensionType)
	{
		return getResolver(extensionType).resolveXml(item);
	}

	@Override
	public IAttachment getAttachmentForUuid(ItemKey itemKey, String attachmentUuid, @Nullable String extensionType)
	{
		final IAttachment attachment = getResolver(extensionType).resolveAttachment(itemKey, attachmentUuid);
		if( attachment == null )
		{
			throw new NotFoundException("Attachment with UUID " + attachmentUuid + " not found on item "
				+ itemKey.toString());
		}
		return attachment;
	}

	@Override
	public int getLiveItemVersion(String uuid, @Nullable String extensionType)
	{
		return getResolver(extensionType).getLiveItemVersion(uuid);
	}

	@Override
	public boolean checkRestrictedAttachment(IItem<?> item, IAttachment attachment, @Nullable String extensionType)
	{
		return getResolver(extensionType).checkRestrictedAttachment(item, attachment);
	}

	@Override
	public boolean canViewRestrictedAttachments(IItem<?> item, String extensionType)
	{
		return getResolver(extensionType).canViewRestrictedAttachments(item);
	}

	@Override
	public boolean canRestrictAttachments(IItem<?> item, @Nullable String extensionType)
	{
		return getResolver(extensionType).canRestrictAttachments(item);
	}

	private ItemResolverExtension getResolver(@Nullable String extensionType)
	{
		final String et = (extensionType == null ? "standard" : extensionType);
		final Map<String, ItemResolverExtension> beanMap = resolverTracker.getBeanMap();
		final ItemResolverExtension itemResolverExtension = beanMap.get(et);
		if( itemResolverExtension == null )
		{
			throw new Error("No item resolver for extension type " + et);
		}
		return itemResolverExtension;
	}
}
