package com.ngw.socket;

import com.ngw.domain.ResponseModel;
import com.ngw.socket.ack.AckESTimeOut;
import com.ngw.util.CustomerType;
import com.ngw.util.SocketUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jodd.util.StringUtil;

public class SocketIOClient {

    private static final Logger logger = Logger.getLogger(SocketIOClient.class.getName());

    private static final String TAG = "SocketIOClient";
    private final int socketFailRetrySum = 3;
    private String url;
    private Set<Customer> customers = new HashSet<>();
    private Customer userCustomer = new Customer();
    private Socket socket;
    public static final String EVENT_REGISTER = "register";
    public static final String EVENT_UNREGISTER = "unRegister";
    public static final String EVENT_BROADCASTINFO = "broadcastInfo";
    public static final String EVENT_INFO = "info";
    private Emitter connectedEmitter;

    enum STATUS {
        CONNECT, REGISTER, UNREGISTER, CLOSE
    }

    private STATUS status = STATUS.CLOSE;

    public static class Customer {
        private CustomerType customerType;
        private String customerId;

        @Override
        public String toString() {
            return "Customer{" +
                    "customerType=" + customerType +
                    ", customerId='" + customerId + '\'' +
                    '}';
        }

        public Customer() {
        }

        public Customer(CustomerType customerType, String customerId) {
            if (customerType == null || StringUtil.isBlank(customerId)) {
                logger.info("customerType or customerId is blank!");
            }
            this.customerType = customerType;
            this.customerId = customerId;
        }

        public CustomerType getCustomerType() {
            return customerType;
        }

        public void setCustomerType(CustomerType customerType) {
            this.customerType = customerType;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }
    }

    public SocketIOClient(String url) {
        this(url, null, null);
    }

    private void addCustomers(Customer... customers) {
        for (Customer customer : customers) {
            this.customers.add(customer);
            if (CustomerType.USER.equals(customer.getCustomerType())) {
                userCustomer = customer;
            }
        }
    }

    private void removeCustomers(Customer... customers) {
        for (Customer customer : customers) {
            this.customers.remove(customer);
            if ("user".equals(customer.getCustomerType().getCustomerType())) {
                userCustomer = new Customer();
            }
        }
    }

    public SocketIOClient(String url, CustomerType customerType, String customerId) {
        this.url = url;
        if (customerType != null && StringUtil.isNotBlank(customerId)) {
            Customer customer = new Customer(customerType, customerId);
            addCustomers(customer);
        }
    }

    public void connection() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = false;
            opts.reconnection = true;
            socket = IO.socket(url, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
        logger.info("socketIO connection!");
    }

    public Emitter onListener(String event, Emitter.Listener listener) {
        //假如还没有人注册，则警告继续
        if (status != STATUS.REGISTER) {
            logger.warning("no one registered!");
        }
        if (connectedEmitter == null) {
            logger.log(Level.SEVERE, "socketIO not connected!");
            return null;
        }
        return connectedEmitter.on(event, listener);
    }

    private void onConnection(final Emitter.Listener listener) {
        connectedEmitter = socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            /**
             * EVENT_CONNECT该方法不允许阻塞
             * @param args
             */
            @Override
            public void call(final Object... args) {
                logger.info("socketIO is connected!");
                status = STATUS.CONNECT;
                listener.call(args);
                socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        logger.warning("socketIO disconnect!");
                    }
                });
            }
        });
        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            /**
             * EVENT_CONNECT_ERROR该方法不允许阻塞
             * @param args
             */
            @Override
            public void call(final Object... args) {
                logger.info("socketIO connect error!\n" + args[0]);
                status = STATUS.CLOSE;
                listener.call(args);
            }
        });
    }

    public void register(final Customer customer, final ISocketEmitCallBack iSocketEmitCallBack) {
        onConnection(new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (status.ordinal() > STATUS.REGISTER.ordinal()) {
                    iSocketEmitCallBack.call(SocketUtil.getBaseFailResponseMap("status: " + status + ",register error,socketIO not connected!"));
                    logger.info("status: " + status + ",register error,socketIO not connected!");
                    return;
                }
                if (customer == null) {
                    iSocketEmitCallBack.call(SocketUtil.getBaseFailResponseMap("customer is empty!"));
                    logger.log(Level.SEVERE, "customer is empty!");
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(customer.customerType.getCustomerType(), customer.customerId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addCustomers(customer);
                emit(EVENT_REGISTER, jsonObject, iSocketEmitCallBack);
            }
        });
    }

    public void unRegister(Customer customer, ISocketEmitCallBack iSocketEmitCallBack) {
        if (!customers.contains(customer)) {
            iSocketEmitCallBack.call(SocketUtil.getBaseFailResponseMap(customer + " not registered!"));
            logger.warning(customer + " not registered!");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(customer.customerType.getCustomerType(), customer.customerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        removeCustomers(customer);
        emit(EVENT_UNREGISTER, jsonObject, iSocketEmitCallBack);
    }

    public void disconnect() {
        socket.disconnect();
        status = STATUS.CLOSE;
    }

    public void send(String event, String targetCustomer, Object object, ISocketEmitCallBack iSocketEmitCallBack) {
        if (status.ordinal() > STATUS.REGISTER.ordinal()) {
            iSocketEmitCallBack.call(SocketUtil.getBaseFailResponseMap("status: " + status + ",send error,socketIO not connected!"));
            logger.info("status: " + status + ",send error,socketIO not connected!");
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("roomName", targetCustomer);
            jsonObject.put("eventName", event);
            jsonObject.put("text", object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        emit(EVENT_BROADCASTINFO, jsonObject, iSocketEmitCallBack);
    }

    private void emit(final String eventName, final Object object, final ISocketEmitCallBack iSocketEmitCallBack) {
        connectedEmitter.emit(eventName, object, new AckESTimeOut() {
            @Override
            public void responseCall(Map<String, String> response) {
                switch (eventName) {
                    case EVENT_REGISTER:
                        status = STATUS.REGISTER;
                    case EVENT_UNREGISTER:
                        if (customers.isEmpty()) {
                            status = STATUS.UNREGISTER;
                        }
                }
                iSocketEmitCallBack.call(response);
            }
        });
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public Customer getUserCustomer() {
        return userCustomer;
    }
}
