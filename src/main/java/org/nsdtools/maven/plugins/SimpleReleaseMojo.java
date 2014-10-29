package org.nsdtools.maven.plugins;

import java.util.regex.Matcher;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.versions.DefaultVersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name = "simple-release", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class SimpleReleaseMojo extends AbstractMojo {

	@Parameter(readonly = true, defaultValue = "${project}")
	private MavenProject project;

	@Parameter(property = "doRelease", required = true)
	private boolean doRelease;

	public void execute() throws MojoExecutionException {
		String version = project.getVersion();
		String newVersion = null;

		DefaultVersionInfo info;
		try {
			info = new DefaultVersionInfo(version);
		} catch (VersionParseException e) {
			throw new MojoExecutionException("Error parsing pom version", e);
		}

		getLog().info("current version::: " + version);

		if (info.isSnapshot() && !doRelease)
			throw new MojoExecutionException("Version is already SNAPSHOT");
		if (!info.isSnapshot() && doRelease)
			throw new MojoExecutionException("Version is already RELEASE");

		if (info.isSnapshot())
		{
			getLog().info("is Snapshot");
			newVersion = info.getReleaseVersionString();
		}
		else
			newVersion = info.getNextVersion().getSnapshotVersionString();

		getLog().info("set newVersion property value: " + newVersion);

		project.getProperties().setProperty("newVersion", newVersion);
		project.getProperties().setProperty("generateBackupPoms", "false");

	}

	public String getReleaseVersionString(final String ver)
	{
		String baseVersion = ver;

		Matcher m = Artifact.VERSION_FILE_PATTERN.matcher(baseVersion);

		if (m.matches())
		{
			getLog().info("matches");
			baseVersion = m.group(1);

		}
		// MRELEASE-623 SNAPSHOT is case-insensitive
		else if (StringUtils.right(baseVersion, 9)
				.equalsIgnoreCase("-" + Artifact.SNAPSHOT_VERSION))
		{

			getLog().info("right");

			baseVersion = baseVersion.substring(0,
					baseVersion.length() - Artifact.SNAPSHOT_VERSION.length() - 1);

		}
		else if (baseVersion.equals(Artifact.SNAPSHOT_VERSION))
		{
			getLog().info("else");
			baseVersion = "1.0";
		}
		getLog().info("baseVersion::" + baseVersion);

		return baseVersion;
	}
}
