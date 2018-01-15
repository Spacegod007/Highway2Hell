package logic.game;

import bootstrapper.Main;
import logic.fontyspublisher.IPropertyListener;
import logic.fontyspublisher.IRemotePropertyListener;
import logic.fontyspublisher.IRemotePublisherForListener;

import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class SubMainRMI extends UnicastRemoteObject implements Serializable, IRemotePropertyListener
{
    private final transient Main application;
    private final IRemotePublisherForListener rpl;

    public SubMainRMI(Main application, IRemotePublisherForListener rpl) throws RemoteException
    {
        this.application = application;
        this.rpl = rpl;
        try
        {
            rpl.subscribeRemoteListener(this, "gameObjects");
        } catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) throws RemoteException
    {
        System.out.println("informed");
        switch (evt.getPropertyName())
        {
            case "gameObjects": application.update((List<GameObject>)evt.getNewValue()); break;
        }
    }
}
