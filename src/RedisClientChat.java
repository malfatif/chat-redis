import java.util.Calendar;

import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisClientChat {

	private static final String CHANNEL = "chat";
	private static String name;
	private static final String REDIS_ADDRESS = "127.0.0.1";
	private static final Integer REDIS_PORT = 6379;
	private static Jedis jedis;
	
	public static void main(String... args){
		askUserName();
		subscribeChannelRedis();
		initChat();
	}

	private static Jedis connectRedis() {
		jedis = new Jedis(REDIS_ADDRESS, REDIS_PORT);
		jedis.connect();
		return jedis;
	}

	private static void initChat() {
		System.out.println("Connected as "+name);
		
		String message = "";
		while(message != null){
			message = JOptionPane.showInputDialog(null, null,  "Connected as "+name +"!. Send your message:", JOptionPane.INFORMATION_MESSAGE);
			sendMessageIfNotEmpty(message);
		}
	}

	private static void subscribeChannelRedis() {
		Jedis jedis = connectRedis();
		Thread thread = new Thread(){
			@Override
			public void run() {
				jedis.subscribe(new JedisPubSub() {
				    @Override
				    public void onMessage(String channel, String message) {
				    	System.out.println(message);
				    }
				}, CHANNEL);	
			}
			
		};
		thread.start();
	}

	private static void sendMessageIfNotEmpty(String message) {
		Jedis jedis = connectRedis();
		if(message ==  null){
			return;
		}
		Calendar c = Calendar.getInstance();
		String hourMinute = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE); 
		
		jedis.publish(CHANNEL,  "("+hourMinute+") "+ name +": " +message);
	}
	
	private static void askUserName() {
		name = JOptionPane.showInputDialog(null, null,  "Type your name:", JOptionPane.INFORMATION_MESSAGE);
	}
	
}
