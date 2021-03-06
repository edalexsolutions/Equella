/*
 * Created on Jul 14, 2004 For "The Learning Edge"
 */
package com.tle.core.item.operations;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import com.tle.core.item.dao.ItemDao;
import com.tle.core.item.dao.ItemIdKeyBatcher;

/**
 * @author jmaginnis
 */
@SuppressWarnings("nls")
public abstract class BaseFilter implements ItemOperationFilter
{
	private Date dateNow;
	private transient WorkflowOperation[] cachedOps;

	@Inject
	private ItemDao itemDao;

	@Override
	public void setDateNow(Date dateNow)
	{
		this.dateNow = dateNow;
	}

	public Date getDateNow()
	{
		return dateNow;
	}

	public String getJoinClause()
	{
		return "";
	}

	@Override
	public final WorkflowOperation[] getOperations()
	{
		if( cachedOps == null )
		{
			cachedOps = createOperations();
		}
		return cachedOps;
	}

	protected abstract WorkflowOperation[] createOperations();

	/**
	 * Should not include an actual 'where' keyword
	 * 
	 * @return
	 */
	public String getWhereClause()
	{
		return "";
	}

	public void queryValues(Map<String, Object> values)
	{
		// To be overridden
	}

	@Override
	public boolean isReadOnly()
	{
		return false;
	}

	@Override
	public FilterResults getItemIds()
	{
		ItemIdKeyBatcher batcher = new ItemIdKeyBatcher(itemDao)
		{
			@Override
			protected String joinClause()
			{
				return getJoinClause();
			}

			@Override
			protected String whereClause()
			{
				return getWhereClause();
			}

			@Override
			protected void addParameters(Map<String, Object> params)
			{
				queryValues(params);
			}
		};

		return new FilterResults(batcher.getTotalCount(), batcher);
	}
}
