package shin.watchdog.config;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import shin.watchdog.processor.GeekhackProcessor;
import shin.watchdog.processor.GroupBuyProcessor;
import shin.watchdog.processor.InterestCheckProcessor;
import shin.watchdog.processor.MechMarketProcessor;
import shin.watchdog.processor.bot.AlertRequestListener;

@Configuration
public class WatchdogConfig {

    @Bean
    public JDA jda(@Autowired BotConfig botConfig) throws LoginException, InterruptedException {
        //System.out.println(botConfig.token);
        JDA jda = new JDABuilder(AccountType.BOT)
            .setToken(botConfig.getToken())
            .addEventListener(new AlertRequestListener())
            .build();

        jda.awaitReady();

        return jda;
    }

    @Bean
    public MechMarketProcessor mechmarketProcessor() {
        return new MechMarketProcessor();
    }
    

    @Bean
    public GeekhackProcessor icProcessor() {
        // https://geekhack.org/index.php?action=.xml;type=atom;boards132;limit=10;sa=news
        return new InterestCheckProcessor(
            "Interest Checks",
            "https://geekhack.org/index.php?action=.xml;type=atom",
            "132",
            "10",
            "news",
            "<@&477264441319096321>",
            "https://discordapp.com/api/webhooks/477261547517902848/eq1z6lMMo4-xdz5WAw3xK9DXKFWBUjPwunbeCHwJbRBYNVToqUailAVEB4-08yc8FyHh"
        );
    }

    @Bean
    public GeekhackProcessor gbProcessor() {
        return new GroupBuyProcessor(
            "Group Buys",
            "https://geekhack.org/index.php?action=.xml;type=atom",
            "70",
            "5",
            "news",
            "<@&477264488983429130>",
            "https://discordapp.com/api/webhooks/477261735271858176/atBPCQzWMAj_k6PVrJTMqggwaoEnQ7Hz4HlHjyp6hmfGrdIKgNEbbD9hMrmUms3Y5hVq"
        );
    }
}