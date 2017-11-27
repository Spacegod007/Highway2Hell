package logic.administration;

import logic.fontyspublisher.IRemotePropertyListener;
import logic.remote_method_invocation.RMIClient;
import javafx.collections.FXCollections;
import sample.Main;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class Administration extends UnicastRemoteObject implements IRemotePropertyListener
{

    private RMIClient rmiClient;
    private User user;
    private Main main;

    public Administration(RMIClient rmiClient) throws RemoteException{
        this.rmiClient = rmiClient;
        this.user = rmiClient.getUser();
        try
        {
            rmiClient.getRpl().subscribeRemoteListener(this, "lobbies");
        }
        catch(RemoteException ex)
        {
            ex.printStackTrace();
        }
    }

    public void setUsername(String username)
    {
        this.setUser(rmiClient.setUsername(username));
        main.setListvwLobby(FXCollections.observableList(rmiClient.getLobbies()));
    }

    public boolean inLobby()
    {
       if(rmiClient.getActiveLobby() != null)
       {
           return true;
       }
       return false;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public boolean joinLobby(Lobby lobby){
        try
        {
            if(rmiClient.joinLobby(lobby))
            {
                rmiClient.setActiveLobby(lobby, rmiClient.getUser().getID());
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean leaveLobby()
    {
        return leaveLobby(rmiClient.getUser().getID());
    }
    public boolean leaveLobby(int leaverId)
    {
        try{
            Lobby lobby = rmiClient.getActiveLobby();

            if(lobby != null)
            {
                if(rmiClient.leaveLobby(lobby.getId(), leaverId))
                {
                    return true;
                }
            }
            else{
                System.out.println("Error: Lobby is null");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Lobby hostLobby(String name)
    {
        Lobby lobby = null;
        try{
            lobby = rmiClient.addLobby(name);
            if(lobby != null)
            {
                rmiClient.setActiveLobby(lobby, rmiClient.getUser().getID());
            }
            return lobby;

        }
        catch(Exception e){
            return lobby;
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        main.setListvwLobby(FXCollections.observableList((List<Lobby>)evt.getNewValue()));
        System.out.println("property changed: " + evt.getPropertyName());
    }

    public void setMain(Main main)
    {
        this.main = main;


    }
}
