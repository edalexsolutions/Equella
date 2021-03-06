package com.tle.core.entity.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.tle.beans.entity.BaseEntity;
import com.tle.beans.entity.LanguageBundle;
import com.tle.core.entity.dao.BaseEntityDao;
import com.tle.core.entity.registry.EntityRegistry;
import com.tle.core.entity.service.AbstractEntityService;
import com.tle.core.entity.service.BaseEntityService;
import com.tle.core.guice.Bind;

/**
 * @author Nicholas Read
 */
@Bind(BaseEntityService.class)
@Singleton
public class BaseEntityServiceImpl implements BaseEntityService
{
	@Inject
	private BaseEntityDao dao;
	@Inject
	private EntityRegistry entityRegistry;

	@Override
	public LanguageBundle getNameForId(long id)
	{
		return dao.getEntityNameForId(id);
	}

	@Override
	public List<String> getEditPrivilegeForEntitiesIHaveNoneToEdit()
	{
		List<String> result = new ArrayList<String>();
		for( AbstractEntityService<?, ? extends BaseEntity> service : entityRegistry.getAllEntityServices() )
		{
			if( service.listEditable().isEmpty() )
			{
				result.add(service.getEditPrivilege());
			}
		}
		return result;
	}

	@Override
	public Map<Long, String> getUuids(Set<Long> ids)
	{
		return dao.getUuids(ids);
	}

	@Override
	public List<Long> getIdsFromUuids(Set<String> uuids)
	{
		return dao.getIdsFromUuids(uuids);
	}
}
