package com.tle.core.institution.migration.v41;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.Query;
import org.hibernate.annotations.AccessType;
import org.hibernate.classic.Session;

import com.dytech.devlib.PropBagEx;
import com.dytech.devlib.PropBagEx.PropBagIterator;
import com.tle.beans.Institution;
import com.tle.common.Check;
import com.tle.core.filesystem.EntityFile;
import com.tle.core.guice.Bind;
import com.tle.core.hibernate.impl.HibernateMigrationHelper;
import com.tle.core.migration.AbstractHibernateDataMigration;
import com.tle.core.migration.MigrationInfo;
import com.tle.core.migration.MigrationResult;
import com.tle.core.services.FileSystemService;

@Bind
@Singleton
public class DisplayTemplateLocationMigration extends AbstractHibernateDataMigration
{
	@Inject
	private FileSystemService fileSystemService;

	@Override
	protected int countDataMigrations(HibernateMigrationHelper helper, Session session)
	{
		Query countQry = session
			.createQuery("SELECT COUNT(*) FROM ItemDefinition WHERE slow.itemSummarySections LIKE :query");
		countQry.setParameter("query", "%summarytemplate%");
		return count(countQry);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void executeDataMigration(HibernateMigrationHelper helper, MigrationResult result, Session session)
		throws Exception
	{
		Query dataQry = session.createQuery("FROM ItemDefinition WHERE slow.itemSummarySections LIKE :query");
		dataQry.setParameter("query", "%summarytemplate%");

		final List<FakeItemDefinition> ids = dataQry.list();

		for( FakeItemDefinition id : ids )
		{
			renameFolder(id, session, result);
		}
	}

	private void renameFolder(FakeItemDefinition id, Session session, MigrationResult result) throws Exception
	{
		final String st = "summarytemplate";
		final String dt = "displaytemplate";

		final String sectionsXml = id.slow.itemSummarySections;

		if( !Check.isEmpty(sectionsXml) )
		{
			// Summary sections XML
			PropBagEx xml = new PropBagEx(sectionsXml);

			// Check through all available Summary Sections
			PropBagIterator iter = xml.iterator("configList/com.tle.beans.entity.itemdef.SummarySectionsConfig");

			for( PropBagEx config : iter )
			{
				boolean changed = false;

				if( config.getNode("value").equals("xsltSection") )
				{
					// Get template folder path
					String folderPath = config.getNode("configuration");
					folderPath = folderPath.substring(0, folderPath.indexOf('/'));
					EntityFile item = new EntityFile(id.id);
					Institution institution = new Institution();
					institution.setFilestoreId(id.institution.shortName);
					item.setInstitution(institution);

					// Rename folder
					fileSystemService.rename(item, folderPath, dt);

					// Update XML
					String newFolderPath = config.getNode("configuration").replace(st, dt);
					config.setNode("configuration", newFolderPath);

					changed = true;
				}

				// Update Database
				if( changed )
				{
					id.slow.itemSummarySections = xml.toString();
					session.update(id);
					session.flush();
				}
			}
		}
		result.incrementStatus();
	}

	@Override
	protected Class<?>[] getDomainClasses()
	{
		return new Class[]{FakeItemdefBlobs.class, FakeItemDefinition.class, FakeBaseEntity.class,
				FakeInstitution.class};
	}

	@Override
	public MigrationInfo createMigrationInfo()
	{
		return new MigrationInfo("com.tle.core.entity.services.displaytemplatelocation.title");
	}

	@Entity(name = "ItemdefBlobs")
	@AccessType("field")
	public static class FakeItemdefBlobs
	{
		@Id
		long id;

		@Lob
		String itemSummarySections;
	}

	@Entity(name = "ItemDefinition")
	@AccessType("field")
	public static class FakeItemDefinition extends FakeBaseEntity
	{
		@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
		FakeItemdefBlobs slow;
	}

	@Entity(name = "BaseEntity")
	@AccessType("field")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class FakeBaseEntity
	{
		@Id
		long id;

		@ManyToOne(fetch = FetchType.EAGER)
		FakeInstitution institution;
	}

	@Entity(name = "Institution")
	public static class FakeInstitution
	{
		@Id
		long id;
		String shortName;
	}
}
