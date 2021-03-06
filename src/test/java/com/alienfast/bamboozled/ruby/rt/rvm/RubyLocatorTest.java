package com.alienfast.bamboozled.ruby.rt.rvm;

import static com.alienfast.bamboozled.ruby.fixtures.RvmFixtures.getJRubyRuntimeDefaultGemSet;
import static com.alienfast.bamboozled.ruby.fixtures.RvmFixtures.getMRIRubyRuntimeDefaultGemSet;
import static com.alienfast.bamboozled.ruby.fixtures.RvmFixtures.getMRIRubyRuntimeRails31GemSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.alienfast.bamboozled.ruby.fixtures.RvmFixtures;
import com.alienfast.bamboozled.ruby.rt.RubyRuntime;
import com.alienfast.bamboozled.ruby.rt.rvm.RvmRubyLocator;
import com.alienfast.bamboozled.ruby.rt.rvm.RvmUtils;
import com.alienfast.bamboozled.ruby.util.EnvUtils;
import com.alienfast.bamboozled.ruby.util.FileSystemHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * RVM locator tests
 */
@RunWith( MockitoJUnitRunner.class )
public class RubyLocatorTest {

    @Mock
    FileSystemHelper fileSystemHelper;

    RvmRubyLocator rvmRubyLocator;

    @Before
    public void setUp() throws Exception {

        this.rvmRubyLocator = new RvmRubyLocator( this.fileSystemHelper, RvmFixtures.getUserRvmInstallation() );
    }

    @Test
    public void testBuildEnv() throws Exception {

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when( this.fileSystemHelper.pathExists( mriRuby.getRubyExecutablePath() ) ).thenReturn( true );
        when( this.fileSystemHelper.pathExists( mriRuby.getGemPath() ) ).thenReturn( true );

        Map<String, String> currentEnvVars = Maps.newHashMap();

        currentEnvVars.put( "PATH", RvmFixtures.TEST_CURRENT_PATH );

        Map<String, String> envVars = this.rvmRubyLocator.buildEnv(
                "ruby-1.9.3-p0@default",
                "/Users/kross/.rvm/versions/ruby-1.9.3-p/bin/ruby",
                currentEnvVars );

        assertTrue( envVars.containsKey( EnvUtils.MY_RUBY_HOME ) );

        assertTrue( envVars.containsKey( EnvUtils.GEM_HOME ) );
        assertEquals( RvmFixtures.GEM_HOME, envVars.get( EnvUtils.GEM_HOME ) );

        assertTrue( envVars.containsKey( EnvUtils.GEM_PATH ) );

        assertTrue( envVars.containsKey( EnvUtils.BUNDLE_HOME ) );
        assertEquals( RvmFixtures.BUNDLE_HOME, envVars.get( EnvUtils.BUNDLE_HOME ) );

        assertTrue( envVars.containsKey( RvmUtils.RVM_RUBY_STRING ) );
        assertEquals( mriRuby.getName(), envVars.get( RvmUtils.RVM_RUBY_STRING ) );

        assertTrue( envVars.containsKey( RvmUtils.RVM_GEM_SET ) );
        assertEquals( mriRuby.getGemSetName(), envVars.get( RvmUtils.RVM_GEM_SET ) );

        assertTrue( envVars.containsKey( EnvUtils.PATH ) );

        assertEquals( RvmFixtures.getMRIRubyRuntimeDefaultBinPath(), envVars.get( "PATH" ) );
    }

    @Test
    public void testGetRubyHome() throws Exception {

    }

    @Test
    public void testGetGemBinPath() throws Exception {

    }

    @Test
    public void testGetRubyRuntime() throws Exception {

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when( this.fileSystemHelper.pathExists( mriRuby.getRubyExecutablePath() ) ).thenReturn( true );
        when( this.fileSystemHelper.pathExists( mriRuby.getGemPath() ) ).thenReturn( true );

        RubyRuntime rubyRuntime;

        rubyRuntime = this.rvmRubyLocator.getRubyRuntime( "ruby-1.9.3-p0@default" );

        assertEquals( mriRuby, rubyRuntime );

        final RubyRuntime jRuby = RvmFixtures.getJRubyRuntimeDefaultGemSet();

        when( this.fileSystemHelper.pathExists( jRuby.getRubyExecutablePath() ) ).thenReturn( true );
        when( this.fileSystemHelper.pathExists( jRuby.getGemPath() ) ).thenReturn( true );

        rubyRuntime = this.rvmRubyLocator.getRubyRuntime( "jruby-1.6.5@default" );

        assertEquals( jRuby, rubyRuntime );
    }

    @Test
    public void testListRubyRuntimes() throws Exception {

        when( this.fileSystemHelper.listPathDirNames( "/home/kross/.rvm/rubies" ) ).thenReturn(
                Lists.newArrayList( "jruby-1.6.5", "ruby-1.9.3-p0" ) );

        when( this.fileSystemHelper.listPathDirNames( "/home/kross/.rvm/gems" ) )
                .thenReturn(
                        Lists.newArrayList(
                                "jruby-1.6.5",
                                "jruby-1.6.5@global",
                                "ruby-1.9.3-p0",
                                "ruby-1.9.3-p0@global",
                                "ruby-1.9.3-p0@rails31" ) );

        final RubyRuntime mriRuby = RvmFixtures.getMRIRubyRuntimeDefaultGemSet();

        when( this.fileSystemHelper.pathExists( mriRuby.getRubyExecutablePath() ) ).thenReturn( true );
        when( this.fileSystemHelper.pathExists( mriRuby.getGemPath() ) ).thenReturn( true );

        final RubyRuntime jRuby = RvmFixtures.getJRubyRuntimeDefaultGemSet();

        when( this.fileSystemHelper.pathExists( jRuby.getRubyExecutablePath() ) ).thenReturn( true );
        when( this.fileSystemHelper.pathExists( jRuby.getGemPath() ) ).thenReturn( true );

        List<RubyRuntime> rubyRuntimeList = this.rvmRubyLocator.listRubyRuntimes();

        final RubyRuntime mriRubyRails31 = RvmFixtures.getMRIRubyRuntimeRails31GemSet();

        when( this.fileSystemHelper.pathExists( mriRubyRails31.getRubyExecutablePath() ) ).thenReturn( true );
        when( this.fileSystemHelper.pathExists( mriRubyRails31.getGemPath() ) ).thenReturn( true );

        assertEquals( 3, rubyRuntimeList.size() );

        assertTrue( rubyRuntimeList.contains( getMRIRubyRuntimeDefaultGemSet() ) );
        assertTrue( rubyRuntimeList.contains( getMRIRubyRuntimeRails31GemSet() ) );
        assertTrue( rubyRuntimeList.contains( getJRubyRuntimeDefaultGemSet() ) );

    }

    @Test
    public void testHasRuby() throws Exception {

        when( this.fileSystemHelper.listPathDirNames( "/home/kross/.rvm/rubies" ) ).thenReturn(
                Lists.newArrayList( "jruby-1.6.5", "ruby-1.9.3-p0" ) );

        assertTrue( this.rvmRubyLocator.hasRuby( "ruby-1.9.3-p0" ) );
        assertTrue( this.rvmRubyLocator.hasRuby( "jruby-1.6.5" ) );
        assertFalse( this.rvmRubyLocator.hasRuby( "ruby-1.9.2-p0" ) );
    }
}
