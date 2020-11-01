package br.gov.sp.fatec.backend.services;

import br.gov.sp.fatec.backend.exceptions.ConversationException.ConversationNotFoundException;
import br.gov.sp.fatec.backend.exceptions.MemberException.MemberNotFoundException;
import br.gov.sp.fatec.backend.exceptions.ConversationException.ConversationCrudException;
import br.gov.sp.fatec.backend.models.Conversation;
import br.gov.sp.fatec.backend.models.Member;
import br.gov.sp.fatec.backend.repositories.ConversationRepository;
import br.gov.sp.fatec.backend.repositories.MemberRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("ConversationService")
@Transactional
public class ConversationServiceImpl implements ConversationService {

  @Autowired
  private ConversationRepository conversationRepository;
  
  @Autowired
  private MemberRepository memberRepository;

  @Override
  public List<Conversation> getAllConversations() {
    return conversationRepository.findAll();
  }

  @Override
  public Conversation getConversationById(long conversationId) throws ConversationNotFoundException {
	  Conversation fetchedChat = conversationRepository.findConversationById(conversationId);

    if(fetchedChat == null) {
      throw new ConversationNotFoundException(conversationId);
    }

	  return fetchedChat;
  }

  @Override
  public Conversation createConversation(Conversation conversation) throws ConversationCrudException {
    Conversation newChat = conversationRepository.save(conversation);
    
    if(newChat == null) {
      throw new ConversationCrudException("erro ao criar uma conversa");
    }

    return newChat;
  }

  @Override
  public Conversation updateConversationById(long conversationId, Conversation chatDataToUpdate) {
    Conversation chat = conversationRepository.findConversationById(conversationId);

    if(chat == null) {
      throw new ConversationNotFoundException(conversationId);
    }

    if(chatDataToUpdate.getTitle() != null) chat.setTitle(chatDataToUpdate.getTitle());
    if(chatDataToUpdate.getMembers() != null) chat.setMembers(chatDataToUpdate.getMembers());
    if(chatDataToUpdate.getMessages() != null) chat.setMessages(chatDataToUpdate.getMessages());

    Conversation updatedChat = conversationRepository.save(chat);

    if(updatedChat == null) {
      throw new ConversationCrudException(String.format("erro ao atualizar os dados da conversa de id = %d", conversationId));
    }

    return updatedChat;
  }

  @Override
  public void deleteConversationById(long conversationId) throws ConversationNotFoundException {
    Conversation chatToDelete = conversationRepository.findConversationById(conversationId);

    if(chatToDelete == null) {
      throw new ConversationNotFoundException(conversationId);
    }

    conversationRepository.deleteById(conversationId);
  }

  @Override
  public Conversation addMemberToConversation(long memberId, long conversationId) throws MemberNotFoundException,
                                                                                         ConversationNotFoundException,
                                                                                         ConversationCrudException {
    Conversation chat = conversationRepository.findConversationById(conversationId);
    
    if(chat == null) {
      throw new ConversationNotFoundException(conversationId);
    }
    
    if(chat.getMembers().stream().filter(member -> member.getId() == memberId).count() > 0) {
      throw new ConversationCrudException("membro já adicionado na conversa");
    }
    
    Member memberToAdd = memberRepository.findMemberById(memberId);

    if(memberToAdd == null) {
      throw new MemberNotFoundException(memberId);
    }

    chat.addMember(memberToAdd);

    conversationRepository.save(chat);

    return chat;
  }

  @Override
  public Conversation removeMemberFromConversation(long memberId, long conversationId) throws MemberNotFoundException, ConversationNotFoundException {
    Conversation chat = conversationRepository.findConversationById(conversationId);
    
    if(chat == null) {
      throw new ConversationNotFoundException(conversationId);
    }

    if(chat.getMembers().stream().filter(member -> member.getId() == memberId).count() == 0) {
      throw new MemberNotFoundException(memberId);
    }
    
    Member memberToRemove = memberRepository.findMemberById(memberId);

    if(memberToRemove == null) {
      throw new MemberNotFoundException(memberId);
    }

    chat.removeMember(memberToRemove);

    conversationRepository.save(chat);

    return chat;
  }

  
}