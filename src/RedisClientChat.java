import java.util.Calendar;

import javax.swing.JOptionPane;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class RedisClientChat {

	private static final String CHANNEL = "chat";
	private static String nome;
	private static final String REDIS_ADDRESS = "127.0.0.1";
	private static final Integer REDIS_PORT = 6379;
	private static Jedis jedis;
	
	public static void main(String... string){
		perguntarNomeDoUsuario();
		subscribeChannelRedis();
		iniciarChat();
	}

	private static Jedis conectarRedis() {
		jedis = new Jedis(REDIS_ADDRESS, REDIS_PORT);
		jedis.connect();
		return jedis;
	}

	private static void iniciarChat() {
		System.out.println("Conectado como "+nome);
		
		String mensagem = "";
		while(mensagem != null){
			mensagem = JOptionPane.showInputDialog(null, null,  "Conectado como "+nome +"!. Envie sua Mensagem:", JOptionPane.INFORMATION_MESSAGE);
			enviarMensagemSeNaoForVazia(mensagem);
		}
	}

	private static void subscribeChannelRedis() {
		Jedis jedis = conectarRedis();
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

	private static void enviarMensagemSeNaoForVazia(String mensagem) {
		Jedis jedis = conectarRedis();
		if(mensagem ==  null){
			return;
		}
		Calendar c = Calendar.getInstance();
		String horaMinuto = c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE); 
		
		jedis.publish(CHANNEL,  "("+horaMinuto+") "+ nome +": " +mensagem);
	}
	
	private static void perguntarNomeDoUsuario() {
		nome = JOptionPane.showInputDialog(null, null,  "Digite seu nome:", JOptionPane.INFORMATION_MESSAGE);
	}
	
}
