package com.tle.core.filesystem;

import com.tle.annotation.NonNullByDefault;
import com.tle.annotation.Nullable;
import com.tle.beans.Institution;
import com.tle.common.PathUtils;
import com.tle.common.filesystem.FileHandleUtils;
import com.tle.common.filesystem.FileSystemHelper;

@NonNullByDefault
public class SubItemFile extends ItemFile
{
	private static final long serialVersionUID = 1L;
	private final ItemFile parent;
	private final String extraPath;

	public SubItemFile(ItemFile parent, String extraPath)
	{
		super(parent.getUuid(), parent.getVersion(), parent.getCollectionUuid());
		this.parent = parent;
		this.extraPath = FileSystemHelper.encode(extraPath);
		FileHandleUtils.checkPath(extraPath);
	}

	@Override
	public void setInstitution(Institution institution)
	{
		super.setInstitution(institution);
		parent.setInstitution(institution);
	}

	@Override
	protected String createAbsolutePath()
	{
		return PathUtils.filePath(parent.getAbsolutePath(), getMyPathComponent());
	}

	@Override
	public String getMyPathComponent()
	{
		return extraPath;
	}

	@Nullable
	@Override
	public String getFilestoreId()
	{
		return parent.getFilestoreId();
	}

	@Override
	public void setFilestoreId(@Nullable String filestoreId)
	{
		parent.setFilestoreId(filestoreId);
	}
}
