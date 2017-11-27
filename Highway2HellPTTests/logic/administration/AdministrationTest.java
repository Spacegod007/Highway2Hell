package logic.administration;

import logic.remote_method_invocation.*;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by maxhe on 23-10-2017.
 */
public class AdministrationTest{


    public AdministrationTest() throws RemoteException
    {

    }
    Administration administration = new Administration(new RMIClient( "test", 1102));

    @Test
    public void getUserTest(){
        User user = new User("Max", 0);
        administration.setUser(user);
        assertEquals(user,administration.getUser());
    }

    @Test
    public void getUserNullTest(){
        administration.setUser(null);
        assertEquals(null,administration.getUser());
    }

    @Test
    public void getUserNotEqualTest(){
        User user = new User("Max", 0);
        administration.setUser(new User("Jan", 1));
        assertNotSame(user,administration.getUser());
    }

    @Test
    public void setUserTest(){
        User user = new User("Max", 0);
        administration.setUser(user);
        assertEquals(user,administration.getUser());
    }

    @Test
    public void setUserNullTest(){
        User user = null;
        administration.setUser(user);
        assertEquals(null,user);
    }

    @Test
    public void setUserNotEqualTest(){
        User user = new User("Max", 0);
        administration.setUser(new User("Jan", 0));
        assertNotSame(user,administration.getUser());
    }

    @Test
    public void hostLobbyTest(){
        assertEquals(true,administration.hostLobby("lobby"));
    }

    @Test
    public void joinLobbyTest(){
        administration.hostLobby("lobby");
        administration.hostLobby("lobby1");
        Lobby lobby = new Lobby("lobby",1, "127.0.0.1");
        assertEquals(true,administration.joinLobby(lobby));
    }
}
