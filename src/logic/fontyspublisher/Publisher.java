/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.fontyspublisher;

import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Publisher. Properties may be registered and unregistered by the domain. Local
 * as well as remote property listeners may subscribe to one or more registered
 * properties. In case a property changes in the domain, the domain should
 * invoke method inform() with the appropriate property. All listeners
 * subscribed to that property will be informed through a (remote) method
 * invocation of propertyChange(). Listeners subscribed to the null-String are
 * by definition subscribed to all properties. Note that (remote) method
 * invocation of propertyChange() is done asynchronously.
 *
 * @author Frank Peeters, Nico Kuijpers
 */
class Publisher
{
    /**
     * Local and remote property listeners subscribed to a property.
     */
    private final Map<String, List<IPropertyListener>> propertyListeners;

    /**
     * String of all registered properties. This string is returned in a
     * RuntimeException in case a listener tries to subscribe to an unknown
     * property (see also method checkInBehalfOfProgrammer).
     */
    private String propertiesString;

    /**
     * Thread pool to inform listeners concurrently. The advantage of using a
     * thread pool is that the number threads is limited and overhead of thread
     * creation is reduced.
     */
    private final ExecutorService pool;

    /**
     * Default no-arg constructor for Publisher.
     */
    public Publisher()
    {
        this(new String[0]);
    }

    /**
     * Constructor for Publisher. Property listeners may subscribe to given
     * properties.
     *
     * @param properties array of properties
     */
    public Publisher(String[] properties)
    {

        // Ensure that hash map is synchronized
        propertyListeners = Collections.synchronizedMap(new HashMap<>());

        // Register null-String as property
        propertyListeners.put(null, Collections.synchronizedList(new ArrayList<>()));

        // Register remaining properties
        for (String s : properties)
        {
            propertyListeners.put(s, Collections.synchronizedList(new ArrayList<>()));
        }

        // Initialize string of all registered properties
        setPropertiesString();

        // Initialize thread pool
        /*
      Number of threads in thread pool.
     */
        int nrThreads = 10;
        pool = Executors.newFixedThreadPool(nrThreads);
    }

    /**
     * Subscribe local property listener. Local listener will be subscribed to
     * given property. In case given property is the null-String, the listener
     * will be subscribed to all properties.
     *
     * @param listener local property listener to be subscribed
     * @param property null-String allowed
     */
    public void subscribeLocalListener(ILocalPropertyListener listener, String property)
    {

        // Subscribe local property listener
        subscribePropertyListener(listener, property);
    }

    /**
     * Subscribe remote property listener. Remote listener will be subscribed to
     * given property. In case given property is the null-String, the listener
     * will be subscribed to all properties.
     *
     * @param listener remote property listener to be subscribed
     * @param property null-String allowed
     */
    public void subscribeRemoteListener(IRemotePropertyListener listener, String property)
    {

        // Subscribe remote property listener
        subscribePropertyListener(listener, property);
    }

    // Subscribe local or remote property listener
    private void subscribePropertyListener(IPropertyListener listener, String property)
    {

        // Check whether property is registered
        checkInBehalfOfProgrammer(property);

        // Subscribe property listener to property
        propertyListeners.get(property).add(listener);
    }

    /**
     * Unsubscribe local property listener. Listener will be
     * unsubscribed from given property. In case given property is the
     * null-string, the listener will be unsubscribed from all properties.
     *
     * @param listener local property listener to be unsubscribed
     * @param property null-String allowed
     */
    public void unsubscribeLocalListener(ILocalPropertyListener listener, String property)
    {
        unsubscribeListener(listener, property);
    }

    /**
     * Unsubscribe remote property listener. Listener will be
     * unsubscribed from given property. In case given property is the
     * null-string, the listener will be unsubscribed from all properties.
     *
     * @param listener remote property listener to be unsubscribed
     * @param property null-String allowed
     */
    public void unsubscribeRemoteListener(IRemotePropertyListener listener, String property)
    {
        unsubscribeListener(listener, property);
    }

    // Unsubscribe local or remote property listener
    private void unsubscribeListener(IPropertyListener listener, String property)
    {
        if (property != null)
        {
            // Unsubscribe property listener from given property
            List<IPropertyListener> listeners = propertyListeners.get(property);
            if (listeners != null)
            {
                listeners.remove(listener);
                propertyListeners.get(null).remove(listener);
            }
        } else
        {
            // Unsubscribe property listener from all propertys
            List<String> keyset = new ArrayList<>(propertyListeners.keySet());
            for (String key : keyset)
            {
                propertyListeners.get(key).remove(listener);
            }
        }
    }

    /**
     * Inform all listeners subscribed to property. All listeners subscribed to
     * given property as well as all listeners subscribed to null-String are
     * informed of a change of given property through a (remote) method
     * invocation of propertyChange(). In case given property is the null-String
     * all subscribed listeners are informed.
     *
     * @param property property is either null-String or is registered
     * @param oldValue original value of property at domain (null is allowed)
     * @param newValue new value of property at domain
     */
    public void inform(String property, Object oldValue, Object newValue)
    {
        // Check whether property is registered
        checkInBehalfOfProgrammer(property);

        // Determine property listeners to be informed
        List<IPropertyListener> listenersToBeInformed;
        listenersToBeInformed = new ArrayList<>();

        if (property != null)
        {
            // Listeners that are subscribed to given property
            listenersToBeInformed.addAll(propertyListeners.get(property));
            // Listeners that are subscribed to null-String
            listenersToBeInformed.addAll(propertyListeners.get(null));
        } else
        {
            // Inform all listeners, including listeners that are subscribed
            // to null-String
            List<String> keyset = new ArrayList<>(propertyListeners.keySet());
            for (String key : keyset)
            {
                listenersToBeInformed.addAll(propertyListeners.get(key));
            }
        }

        // Inform property listeners concurrently
        for (IPropertyListener listener : listenersToBeInformed)
        {

            // Define property change event to be sent to listener
            final PropertyChangeEvent event = new PropertyChangeEvent(
                    this, property, oldValue, newValue);

            // Create command (runnable) to be executed by thread pool
            InformListenerRunnable informListenerRunnable
                    = new InformListenerRunnable(listener, event);

            // Execute command at some time in the future
            pool.execute(informListenerRunnable);
        }
    }

    /**
     * Register property. Register property at this publisher. From now on
     * listeners can subscribe to this property. Nothing changes in case given
     * property was already registered.
     *
     * @param property empty string not allowed
     */
    public void registerProperty(String property)
    {
        if (property.equals(""))
        {
            throw new RuntimeException("a property cannot be an empty string");
        }

        if (propertyListeners.containsKey(property))
        {
            return;
        }

        propertyListeners.put(property, Collections.synchronizedList(new ArrayList<>()));

        setPropertiesString();
    }

    /**
     * Unregister property. Unregister property at this publisher. From now on
     * listeners subscribed to this property will not be informed on changes. In
     * case given property is null-String, all properties (except null) will be
     * unregistered.
     *
     * @param property registered property at this publisher
     */
    public void unregisterProperty(String property)
    {
        // Check whether property is registered
        checkInBehalfOfProgrammer(property);

        if (property != null)
        {
            // Unsubscribe listeners from this property
            propertyListeners.remove(property);
        } else
        {
            List<String> keyset = new ArrayList<>(propertyListeners.keySet());
            for (String key : keyset)
            {
                if (key != null)
                {
                    propertyListeners.remove(key);
                }
            }
        }

        setPropertiesString();
    }

    // Set string of all registered properties 
    private void setPropertiesString()
    {
        List<String> properties = getProperties();
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        boolean firstProperty = true;
        for (String property : properties)
        {
            if (firstProperty)
            {
                firstProperty = false;
            } else
            {
                sb.append(", ");
            }
            sb.append(property);
        }
        sb.append(" }");
        propertiesString = sb.toString();
    }

    // Check whether property is registered
    private void checkInBehalfOfProgrammer(String property)
            throws RuntimeException
    {
        if (!propertyListeners.containsKey(property))
        {
            throw new RuntimeException("property " + property + " is not a "
                    + "published property, please make a choice out of: "
                    + propertiesString);
        }
    }

    /**
     * Obtain all registered properties. An unmodifiable list all properties
     * including the null property is returned.
     *
     * @return list of registered properties including null
     */
    public List<String> getProperties()
    {
        List<String> properties = new ArrayList<>(propertyListeners.keySet());
        return Collections.unmodifiableList(properties);
    }

    // Inner class to enable concurrent method invocation of propertyChange()
    private class InformListenerRunnable implements Runnable
    {

        // Property listener to be informed
        final IPropertyListener listener;

        // Property change event to be sent to listener
        final PropertyChangeEvent event;

        InformListenerRunnable(IPropertyListener listener, PropertyChangeEvent event)
        {
            this.listener = listener;
            this.event = event;
        }

        @Override
        public void run()
        {
            if (listener instanceof ILocalPropertyListener)
            {
                // Property listener is local
                ILocalPropertyListener localListener = (ILocalPropertyListener) listener;
                localListener.propertyChange(event);
            } else
            {
                // Property listener is remote
                IRemotePropertyListener remoteListener = (IRemotePropertyListener) listener;
                try
                {
                        remoteListener.propertyChange(event);

                } catch (RemoteException ex)
                {
                    // No connection to remote property listener
                    unsubscribeListener(listener, null);
                    Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
