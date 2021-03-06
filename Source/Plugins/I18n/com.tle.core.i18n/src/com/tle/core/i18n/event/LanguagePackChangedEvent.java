package com.tle.core.i18n.event;

import java.util.Locale;

import com.tle.core.events.ApplicationEvent;
import com.tle.core.i18n.event.listener.LanguagePackChangedListener;

/**
 * @author Nicholas Read
 */
public class LanguagePackChangedEvent extends ApplicationEvent<LanguagePackChangedListener>
{
	private static final long serialVersionUID = 1L;

	private final Locale locale;

	public LanguagePackChangedEvent(Locale locale)
	{
		super(PostTo.POST_TO_OTHER_CLUSTER_NODES);
		this.locale = locale;
	}

	public Locale getLocale()
	{
		return locale;
	}

	@Override
	public Class<LanguagePackChangedListener> getListener()
	{
		return LanguagePackChangedListener.class;
	}

	@Override
	public void postEvent(LanguagePackChangedListener listener)
	{
		listener.languageChangedEvent(this);
	}
}
