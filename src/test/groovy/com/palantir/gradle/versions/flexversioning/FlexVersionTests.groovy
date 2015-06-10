package com.palantir.gradle.versions.flexversioning

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

class FlexVersionTests {

    private static final String projectName = "flex-testing";
    private static final String tag = "1.5.2";

    static File repoFolder;
    static Git git;
    static Repository repo;
    static String headShaShort;
    static int commits = 0;

    Project project;

    @BeforeClass
    public static void setUpRepo() {
        repoFolder = new File("${System.properties['user.dir']}/${projectName}");
        repoFolder.deleteDir();
        repoFolder.mkdirs();
        
        git = Git.init().setDirectory(repoFolder).call();
        repo = new FileRepositoryBuilder().findGitDir(repoFolder).build();
        
        def commitMessages = [
                "Initial commit",
                "We can keep adding commits",
                "Add some more information",
                "This is a refactor",
                "Final commit"
            ]
        
        for (message in commitMessages) {
            new File("${repoFolder}/myfile.txt").append("${System.currentTimeMillis()} - ${message}\n");
            git.add().addFilepattern("myfile.txt").call();
            git.commit().setMessage(message).call();
            commits++;
        }
        commits--; // The HEAD commit is not counted
        
        RevCommit head = new RevWalk(repo).parseCommit(repo.resolve(Constants.HEAD));
        headShaShort = head.name().substring(0,12);
        
        git.tag().setAnnotated(true).setName(tag).setMessage("This is the ${tag} release...bugfix!").setObjectId(head).call();
    }
    
    @Before
    public void setUpProject() {
        project = ProjectBuilder.builder().withProjectDir(repoFolder).withName(projectName).build();
        new FlexVersionPlugin().apply(project);
    }
    
    @Test
    public void testBasic() {
        assert "master-${commits}-g${headShaShort}" == project.flexVersion().toString()
    }
    
    @Test
    @Ignore
    public void testGlobalOverride() {
        //TODO: Can't actually do this (yet) since it would ruin the other tests.
        // Might require something clever later
    }
    
    @Test
    public void testUserDomain() {
        assert "myownchoice-${commits}-g${headShaShort}" == project.flexVersion("myownchoice").toString()
    }
    
    @Test
    public void testTag() {
        project.flexversion.useTags = true
        assert tag == project.flexVersion().toString()
        assert tag == project.flexVersion("myownchoice").toString()
    }
    
    @Test
    public void testEnvironmentBasic() {
        project.flexversion.envvarSources << "buildRef"
        assert "${System.env['buildRef']}-${commits}-g${headShaShort}" == project.flexVersion().toString()
    }
}
