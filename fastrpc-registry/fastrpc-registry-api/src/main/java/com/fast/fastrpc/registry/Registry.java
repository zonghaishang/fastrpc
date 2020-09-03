package com.fast.fastrpc.registry;

import com.fast.fastrpc.common.Host;
import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : Registry.java, v 0.1 2020-09-01
 */
public interface Registry extends Host {

    /**
     * Register data, such as : provider service, consumer address and other data.
     * <p>
     * Register contracts:<br>
     * 1. When the URL sets the check=false, if failed to register, the exception is not thrown and retried in the background, else the exception will be thrown.<br>
     * 2. When URL sets the dynamic=false, it needs to be stored persistently, otherwise, it should be deleted automatically when the registrant has an abnormal exit.<br>
     * 3. When the URL sets category=routers, it means classified storage, the default category is providers, and the data can be notified by the classified section. <br>
     * 4. When the registry is restarted, network jitter, data can not be lost.<br>
     *
     * @param url Registration information
     */
    void register(URL url);

    /**
     * Unregister data, such as : provider service, consumer address and other data.
     * <p>
     * Unregister contracts:<br>
     * 1. Unregister according to the full url match.<br>
     *
     * @param url Registration information
     */
    void unregister(URL url);

    /**
     * Subscribe to eligible registered data and automatically push when the registered data is changed.
     * <p>
     * Subscribe contracts:<br>
     * 1. When the URL sets the check=false, if failed to subscribe, the exception is not thrown and retried in the background, else the exception will be thrown.<br>
     * 2. Allow interface, uniqueId, and classifier as a conditional query, e.g.: interface=org.apache.dubbo.foo.BarService&uniqueId=1.0.0<br>
     * 3. When the registry is restarted and network jitter, it is necessary to automatically restore the subscription request.<br>
     * 4. The subscription process must be blocked, when the first notice is finished and then returned.<br>
     *
     * @param url      Subscription condition, not allowed to be empty
     * @param listener A listener of the change event, not allowed to be empty
     */
    void subscribe(URL url, RegistryListener listener);

    /**
     * Unsubscribe
     * <p>
     * Unsubscribe contracts:<br>
     * 1. If don't subscribe, ignore it directly.<br>
     * 2. Unsubscribe by full URL match.<br>
     *
     * @param url      Subscription condition, not allowed to be empty
     * @param listener A listener of the change event, not allowed to be empty
     */
    void unsubscribe(URL url, RegistryListener listener);

    /**
     * Publish service data to the registry by application dimension.
     */
    void online();

    /**
     * Removes data from the registry by application dimension.
     */
    void offline();
}
