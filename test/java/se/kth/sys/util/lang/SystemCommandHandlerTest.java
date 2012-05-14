package se.kth.sys.util.lang;

import static org.junit.Assert.*;

//import java.io.File;
//import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

public class SystemCommandHandlerTest {

    @Ignore("Fails to load classes in hudson") @Test
    public void testPrependString() {
//      SystemCommandHandler c1 = new SystemCommandHandler(new String[]{"foo", "bar"});
//      SystemCommandHandler c2 = new SystemCommandHandler(new String[]{"bar"});
//      c2.prepend("foo");
//      if (! c1.testingGetCmdLineStringList().equals(c2.testingGetCmdLineStringList()))
//      fail("prepend(String) did the wrong thing");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testPrependStringArray() {
//      SystemCommandHandler c1 = new SystemCommandHandler(new String[]{"foo", "bar", "baz"});
//      SystemCommandHandler c2 = new SystemCommandHandler(new String[]{"baz"});
//      c2.prepend(new String[]{"foo", "bar"});
//      if (! c1.testingGetCmdLineStringList().equals(c2.testingGetCmdLineStringList()))
//      fail("prepend(String[]) did the wrong thing");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testAppend() {
//      SystemCommandHandler c1 = new SystemCommandHandler(new String[]{"foo", "bar", "baz"});
//      SystemCommandHandler c2 = new SystemCommandHandler(new String[]{"foo"});
//      c2.append("bar");
//      c2.append("baz");
//      if (! c1.testingGetCmdLineStringList().equals(c2.testingGetCmdLineStringList()))
//      fail("append() did the wrong thing");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testExecuteAndWait() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"echo", "foo"});
//      try {
//      c.executeAndWait();
//      } catch (IOException e) {
//      fail("IOException caught: " + e.getMessage());
//      } catch (InterruptedException e) {
//      fail("InterruptedException caught: " + e.getMessage());
//      }
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testSetPassword() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"foo", "XXX"});
//      c.setPassword(1, "gazonk");
//      c.prepend("echo");
//      c.append("bar");
//      c.enableStdOutStore();
//      try {
//      c.executeAndWait();
//      } catch (IOException e) {
//      fail("IOException caught: " + e.getMessage());
//      } catch (InterruptedException e) {
//      fail("InterruptedException caught: " + e.getMessage());
//      }
//      if (! c.getStdOutStore().get(0).equals("foo gazonk bar"))
//      fail("setPassword() handled wrong");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testGetExitCodeZero() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"echo", "foo"});
//      try {
//      c.executeAndWait();
//      } catch (IOException e) {
//      fail("IOException caught: " + e.getMessage());
//      } catch (InterruptedException e) {
//      fail("InterruptedException caught: " + e.getMessage());
//      }
//      if (c.getExitCode() != 0)
//      fail("Executing 'echo foo' did not return exit code 0.");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testGetExitCodeNonZero() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"false"});
//      try {
//      c.executeAndWait();
//      } catch (IOException e) {
//      fail("IOException caught: " + e.getMessage());
//      } catch (InterruptedException e) {
//      fail("InterruptedException caught: " + e.getMessage());
//      }
//      if (c.getExitCode() != 1)
//      fail("Executing 'false' did not return exit code 1.");
        fail("This test is broken somehow.");
    }

    /* don't know how to generate these */
//  @Test
//  public void testGetStdOutIOException() {
//  fail("Not yet implemented"); // TODO
//  }
//  @Test
//  public void testGetStdErrIOException() {
//  fail("Not yet implemented"); // TODO
//  }

    @Ignore("Fails to load classes in hudson") @Test
    public void testStdIO() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"false"});
//      c.enableStdOutStore();
//      c.enableStdErrStore();
//      c.receiveLine(SystemCommandHandler.STDOUT, "out");
//      c.receiveLine(SystemCommandHandler.STDERR, "err");
//      if (! c.getStdOutStore().get(0).equals("out"))
//      fail("did not catch stdout");
//      if (! c.getStdErrStore().get(0).equals("err"))
//      fail("did not catch stderr");
        fail("This test is broken somehow.");
    }

    @Test
    public void testToString() {
        SystemCommandHandler c = new SystemCommandHandler(new String[]{"cat", "my file"});
        // The output should be quoted just like so:
        assertEquals("Command line: cat \"my file\"", c.toString());
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testAsEscapedString() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"sed", "-e", "s/\\\\/\\//g;", "somefile"});
//      if (! c.asEscapedString().equals("sed -e s/\\\\\\\\/\\\\//g\\; somefile"))
//      fail("command was escaped the wrong way");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testSetDirectory() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"pwd"});
//      c.setDirectory(new File("/tmp"));
//      c.enableStdOutStore();
//      try {
//      c.executeAndWait();
//      } catch (IOException e) {
//      fail("IOException caught: " + e.getMessage());
//      } catch (InterruptedException e) {
//      fail("InterruptedException caught: " + e.getMessage());
//      }
//      if (! c.getStdOutStore().get(0).equals("/tmp"))
//      fail("setDirectory() failed");
        fail("This test is broken somehow.");
    }

    @Ignore("Fails to load classes in hudson") @Test
    public void testEnvironment() {
//      SystemCommandHandler c = new SystemCommandHandler(new String[]{"env"});
//      c.environment().put("FOO", "bar");
//      c.enableStdOutStore();
//      try {
//      c.executeAndWait();
//      } catch (IOException e) {
//      fail("IOException caught: " + e.getMessage());
//      } catch (InterruptedException e) {
//      fail("InterruptedException caught: " + e.getMessage());
//      }
//      for (String line : c.getStdOutStore()) {
//      if (line.equals("FOO=bar"))
//      return;
//      }
//      fail("Setting an environment variable had no effect.");
        fail("This test is broken somehow.");
    }

}
