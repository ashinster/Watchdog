package shin.watchdog.processor.bot;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * AlertRequestListener
 */
public class AlertRequestListener extends ListenerAdapter{
    final static Logger logger = LoggerFactory.getLogger(AlertRequestListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Check type of message
        if(event.isFromType(ChannelType.TEXT)) {

            // Get the text channel
            TextChannel textChannel = event.getTextChannel();

            // Check what channel message was posted in and perform action
            if(textChannel.getName().equals("alert-request") || textChannel.getName().equals("debug-room")){  

                // Get the message as a string
                String msg = event.getMessage().getContentDisplay();

                // Check for bot commands
                if(msg.toLowerCase().startsWith("!alert") || msg.toLowerCase().startsWith("!unalert")) {
                    
                    // Get the user/author of the message
                    User discordUser = event.getAuthor();

                    // Get the member of the server
                    Member member = event.getMember();

                    // Get what role the user wants to add
                    String request = msg.substring("!alert".length()).trim();
                    if(msg.toLowerCase().startsWith("!unalert")) {
                        request = msg.substring("!unalert".length()).trim();
                    }

                    // Get the user's current roles
                    List<Role> memberRoles = member.getRoles();

                    // Find the requested role from the list of server's roles
                    List<Role> serverRoles = event.getJDA().getRolesByName(request, true); 

                    // If list is empty then that role does not exist
                    if(serverRoles.isEmpty() || (!serverRoles.get(0).getName().equalsIgnoreCase("group buys") && !serverRoles.get(0).getName().equalsIgnoreCase("interest checks"))){ 
                        logger.info("User {} requested an invalid role: {}", discordUser.getAsTag(), request);   
                        textChannel.sendMessageFormat("%s The specified role is invalid: `%s`", member.getAsMention(), request).queue();  
                    } else {
                        Role requestedRole = serverRoles.get(0); // The role to give or remove from the user

                        if(msg.toLowerCase().startsWith("!alert")) {
                            // Check if the member has the requested role already
                            if (memberRoles.parallelStream().filter(role -> role.getName().equals(requestedRole.getName())).findAny().isPresent()){
                                // Role already added flow
                                logger.info("User {} already has the `{}` alert role!", discordUser.getAsTag(), request);                   
                                textChannel.sendMessageFormat("User %s already has the `%s` alert role", member.getAsMention(), requestedRole.getName()).queue();         
                            } else {
                                // Add role flow
                                event.getGuild().getController().addRolesToMember(member, requestedRole).queue();
                                textChannel.sendMessageFormat("%s Added to `%s` alert role!", discordUser.getAsMention(), requestedRole.getName()).queue();
                                logger.info("Added {} to `{}` alerts", discordUser.getAsTag(), requestedRole.getName());
                            }
                        } else {
                            // Check if the member has the requested role
                            if (!memberRoles.parallelStream().filter(role -> role.getName().equals(requestedRole.getName())).findAny().isPresent()){
                                // Role already added flow
                                logger.info("User {} does not have the `{}` alert role!", discordUser.getAsTag(), request);                   
                                textChannel.sendMessageFormat("User %s does not have the `%s` alert role", member.getAsMention(), requestedRole.getName()).queue();         
                            } else {
                                // Delete role flow
                                event.getGuild().getController().removeRolesFromMember(member, requestedRole).queue();
                                textChannel.sendMessageFormat("%s Removed `%s` alert role!", discordUser.getAsMention(), requestedRole.getName()).queue();
                                logger.info("Removed {} from `{}` alerts", discordUser.getAsTag(), requestedRole.getName());
                            }
                        }
                    }
                }
            }
        }
    }

}