package com.example.community.dao;

import com.example.community.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Object> {
    List<Message> findAllByTypeNotAndReceiverID(int type,String receiverID);
    List<Message> findAllBySenderID(String senderID);
    List<Message> findAllByReceiverID(String receiverID);
    List<Message> findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderIDAndType(String senderID1,String receiverID1,String senderID2,String receiverID2,int type);
    List<Message> findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(String senderID1,String receiverID1,String senderID2,String receiverID2);
    List<Message> findAllBySenderIDOrReceiverIDAndType(String senderID,String receiverID, int type);
}
