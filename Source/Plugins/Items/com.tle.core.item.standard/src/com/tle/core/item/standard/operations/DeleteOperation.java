/*
 * Created on Jul 12, 2004 For "The Learning Edge"
 */
package com.tle.core.item.standard.operations;

import com.tle.beans.item.Item;
import com.tle.beans.item.ItemStatus;
import com.tle.beans.item.ModerationStatus;
import com.tle.core.item.standard.operations.workflow.TaskOperation;
import com.tle.core.security.impl.SecureItemStatus;
import com.tle.core.security.impl.SecureOnCall;

/**
 * @author jmaginnis
 */
@SecureOnCall(priv = "DELETE_ITEM")
@SecureItemStatus(value = ItemStatus.DELETED, not = true)
public class DeleteOperation extends TaskOperation
{
	@Override
	public boolean execute()
	{
		ModerationStatus moderationStatus = getModerationStatus();
		moderationStatus.setDeletedStatus(getItemStatus());
		Item item = getItem();
		moderationStatus.setDeletedModerating(item.isModerating());
		setState(ItemStatus.DELETED);
		exitTasksForItem();
		item.setModerating(false);
		return true;
	}

	@Override
	public boolean isDeleteLike()
	{
		return true;
	}
}
