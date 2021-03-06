package com.tle.core.item.standard.task;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.inject.Inject;
import com.tle.core.guice.Bind;
import com.tle.core.item.service.ItemService;
import com.tle.core.item.standard.FilterFactory;
import com.tle.core.scheduler.ScheduledTask;

/**
 * @author Nicholas Read
 */
@Bind
@Singleton
public class RemoveDeletedItems implements ScheduledTask
{
	@Inject
	private ItemService itemService;
	@Inject
	private FilterFactory filterFactory;
	@Inject(optional = true)
	// can be overrode by the optional-config.properties
	@Named("com.tle.core.tasks.RemoveDeletedItems.daysBeforeRemoval")
	private int daysBeforeRemoval = 7;

	@Override
	public void execute()
	{
		itemService.operateAll(filterFactory.removeDeleted(daysBeforeRemoval));
	}
}
