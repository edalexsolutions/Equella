package com.tle.core.institution.migration.v41;

import java.util.List;

import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.AccessType;
import org.hibernate.classic.Session;

import com.tle.core.guice.Bind;
import com.tle.core.hibernate.impl.HibernateMigrationHelper;
import com.tle.core.migration.AbstractHibernateSchemaMigration;
import com.tle.core.migration.MigrationInfo;
import com.tle.core.migration.MigrationResult;

@Bind
@Singleton
public class BadUrlFieldSizeMigration extends AbstractHibernateSchemaMigration
{
	@Override
	protected int countDataMigrations(HibernateMigrationHelper helper, Session session)
	{
		return 1;
	}

	@Override
	protected List<String> getAddSql(HibernateMigrationHelper helper)
	{
		return helper.getAddColumnsSQL("badurl", "url2");
	}

	@Override
	protected void executeDataMigration(HibernateMigrationHelper helper, MigrationResult result, Session session)
	{
		session.createQuery("UPDATE BadURL SET url2 = url").executeUpdate();
	}

	@Override
	protected List<String> getDropModifySql(HibernateMigrationHelper helper)
	{
		List<String> sql = helper.getDropColumnSQL("badurl", "url");
		sql.addAll(helper.getRenameColumnSQL("badurl", "url2", "url"));
		return sql;
	}

	@Override
	protected Class<?>[] getDomainClasses()
	{
		return new Class[]{FakeBadURL.class};
	}

	@Override
	public MigrationInfo createMigrationInfo()
	{
		return new MigrationInfo("com.tle.core.entity.services.badurl.urltype.title");
	}

	@Entity(name = "BadURL")
	@AccessType("field")
	public static class FakeBadURL
	{
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		long id;

		@Lob
		String url;

		@Lob
		String url2;
	}
}
