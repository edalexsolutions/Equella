/*
 * Created on 8/03/2006
 */
package com.tle.core.item.standard.operations;

import com.tle.beans.item.ItemPack;

public class ParamOverrideOperation extends AbstractStandardWorkflowOperation
{
	private final ItemPack pack;

	public ParamOverrideOperation(ItemPack pack)
	{
		this.pack = pack;
	}

	@Override
	public boolean execute()
	{
		getParams().setItemPack(pack);
		return false;
	}
}
