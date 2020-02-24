package mequie.app.domain.catalogs;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import mequie.app.domain.User;
import mequie.app.domain.Message;

/**
 * 
 * @author 51021 Pedro Marques,51110 Marcelo Mouta,51468 Bruno Freitas
 */
public class MessageBox {

	// Pega num id de user e devolve a ultima msg lida por esse user
    Map<String, Message> userLastMessageRead = new HashMap<>();
    // mensagens do grupo
    List<Message> messages = new ArrayList<>();

    public List<Message> getNewMessagesNotReadByUser(User user) {
        // Vai buscar ultima mensagem vista pelo user
        Message lastMessageRead = this.userLastMessageRead.get(user.getUserID());
        // Procura pela posicao dessa Message nas messages
        int pos = this.messages.indexOf(lastMessageRead);
        // Devolve apenas as mensagens a seguir a essa = tds as msgs n lidas
        List<Message> newMessages = this.messages.subList(pos + 1, this.messages.size());
        // Atualiza a ultima mensagem lida
        int lastPos = this.messages.size() - 1;
        this.userLastMessageRead.put(user.getUserID(), this.messages.get(lastPos));
        
        return newMessages;
    }
}