package com.tle.web.workflow.tasks;

import java.io.IOException;
import java.util.List;

import com.tle.common.usermanagement.user.CurrentUser;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;

import com.dytech.edge.queries.FreeTextQuery;
import com.tle.common.searching.Search.SortType;
import com.tle.common.searching.SortField;
import com.tle.common.searching.SortField.Type;
import com.tle.core.workflow.freetext.CustomLuceneSortComparator;
import com.tle.core.workflow.freetext.TasksIndexer;
import com.tle.web.search.event.FreetextSearchEvent;
import com.tle.web.search.sort.AbstractSortOptionsSection;
import com.tle.web.search.sort.SortOption;
import com.tle.web.sections.SectionInfo;
import com.tle.web.sections.equella.annotation.PlugKey;
import com.tle.web.sections.render.Label;

@SuppressWarnings("nls")
public class TaskSortSection extends AbstractSortOptionsSection<FreetextSearchEvent>
{
	private static final SortField DUEDATE_SORT = new SortField(TasksIndexer.FIELD_DUEDATE, false, Type.LONG);
	private static final SortField ASSIGNEE_SORT = new SortField(FreeTextQuery.FIELD_WORKFLOW_ASSIGNEDTO, false,
		Type.STRING);
	private static final SortField WAITING_SORT = new SortField(TasksIndexer.FIELD_STARTED, true, Type.LONG);
	private static final SortField WORKFLOW_SORT = new SortField(TasksIndexer.FIELD_WORKFLOW, false, Type.STRING);

	private static final String VAL_PRIORITY = "priority";
	@PlugKey("sort." + VAL_PRIORITY)
	private static Label LABEL_PRIORITY;

	private static final String VAL_DUEDATE = "duedate";
	@PlugKey("sort." + VAL_DUEDATE)
	private static Label LABEL_DUEDATE;

	private static final String VAL_WAITING = "waiting";
	@PlugKey("sort." + VAL_WAITING)
	private static Label LABEL_WAITING;

	private static final String VAL_ASSIGNEE = "assignee";
	@PlugKey("sort." + VAL_ASSIGNEE)
	private static Label LABEL_ASSIGNEE;

	private static final String VAL_WORKFLOW = "workflow";
	@PlugKey("sort." + VAL_WORKFLOW)
	private static Label LABEL_WORKFLOW;

	@Override
	protected void addSortOptions(List<SortOption> sorts)
	{
		sorts.add(new SortOption(LABEL_PRIORITY, VAL_PRIORITY, TasksIndexer.FIELD_PRIORITY, true)
		{
			@Override
			public SortField[] createSort()
			{
				return getDefaultSortFields();
			}
		});
		sorts.add(new SortOption(LABEL_DUEDATE, VAL_DUEDATE, DUEDATE_SORT));
		sorts.add(new SortOption(LABEL_ASSIGNEE, VAL_ASSIGNEE, ASSIGNEE_SORT));
		sorts.add(new SortOption(LABEL_WAITING, VAL_WAITING, WAITING_SORT));
		sorts.add(new SortOption(LABEL_WORKFLOW, VAL_WORKFLOW, WORKFLOW_SORT));
		sorts.add(new SortOption(SortType.NAME));
	}

	@Override
	protected String getDefaultSearch(SectionInfo info)
	{
		return getDefaultSortFields()[0].getField();
	}

	private SortField[] getDefaultSortFields()
	{
		SortField prioritySortField = new SortField(TasksIndexer.FIELD_PRIORITY, true, Type.STRING).clone();

		// AssignedTo sort has the custom comparator which always rates
		// currentUser as above <empty> and others. Lucene's custom comparator
		// doesn't allow for reverse
		SortField customisedAssignedToSortField = new SortField(FreeTextQuery.FIELD_WORKFLOW_ASSIGNEDTO,
			createCurrentUserFirstComparator()).clone();

		SortField waitingSortField = new SortField(TasksIndexer.FIELD_STARTED, true, Type.LONG).clone();

		return new SortField[]{prioritySortField, customisedAssignedToSortField, waitingSortField};
	}

	private FieldComparatorSource createCurrentUserFirstComparator()
	{
		return new FieldComparatorSource()
		{

			@Override
			public FieldComparator<Integer> newComparator(final String fieldName, final int numHits, final int sortPos,
				boolean reversed) throws IOException
			{
				return new CustomLuceneSortComparator(numHits, fieldName, CurrentUser.getUserID());
			}
		};
	}
}
