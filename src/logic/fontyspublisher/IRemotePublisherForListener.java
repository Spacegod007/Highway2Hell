/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.fontyspublisher;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * IRemotePublisherForListener. Interface to remote publisher in order to subscribe 
 * and unsubscribe listeners to properties. In case a property changes in the domain, 
 * all listeners subscribed to that property will be informed through a (remote) 
 * method invocation of propertyChange(). Listeners subscribed to the null-String 
 * are by definition subscribed to all properties.
 * 
 * @author Frank Peeters, Nico Kuijpers
 */
public interface IRemotePublisherForListener extends Remote {
    
    /**
     * Subscribe remote property listener. Remote listener will be subscribed 
     * to given property. In case given property is the null-String, the listener
     * will be subscribed to all properties.
     *
     * @param listener remote property listener to be subscribed
     * @param property null-String allowed
     * @throws java.rmi.RemoteException
     */
    void subscribeRemoteListener(IRemotePropertyListener listener, String property)
            throws RemoteException;

    /**
     * Unsubscribe remote property listener. Remote listener will be unsubscribed 
     * from given property. In case given property is the null-string, the listener
     * will be unsubscribed from all properties.
     * 
     * @param listener property listener to be unsubscribed
     * @param property null-String allowed
     * @throws java.rmi.RemoteException
     */
    void unsubscribeRemoteListener(IRemotePropertyListener listener, String property)
            throws RemoteException;
}
