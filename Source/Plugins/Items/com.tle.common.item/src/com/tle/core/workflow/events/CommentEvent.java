/*
 * Created on Apr 21, 2005 For "The Learning Edge"
 */
package com.tle.core.workflow.events;

import com.tle.beans.item.HistoryEvent;

/**
 * @author jmaginnis
 */
public class CommentEvent extends WorkflowEvent
{
	private static final long serialVersionUID = 1L;

	public CommentEvent(HistoryEvent event)
	{
		super(event);
	}

	@Override
	public String getComment()
	{
		return event.getComment();
	}

	public void setComment(String comment)
	{
		event.setComment(comment);
	}
}
