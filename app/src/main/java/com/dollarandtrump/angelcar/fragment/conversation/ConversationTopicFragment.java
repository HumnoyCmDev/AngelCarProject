package com.dollarandtrump.angelcar.fragment.conversation;

import com.dollarandtrump.angelcar.dao.MessageCollectionDao;
import com.dollarandtrump.angelcar.dao.MessageDao;
import com.dollarandtrump.angelcar.sql.QueryMessage;
import com.dollarandtrump.angelcar.interfaces.OnClickItemMessageListener;
import com.dollarandtrump.angelcar.manager.MessageManager;
import com.dollarandtrump.angelcar.sql.MessageSqlTemplate;
import com.squareup.otto.Subscribe;

import java.util.List;



@SuppressWarnings("unused")
public class ConversationTopicFragment extends ConversationFactory implements OnClickItemMessageListener{

    public ConversationTopicFragment() {
        setTopic(true);
    }

    @Override
    public MessageCollectionDao getMessageManager(MessageManager manager) {
//        return manager.getConversationTopic();
        MessageCollectionDao dao = new MessageCollectionDao();
        MessageSqlTemplate<MessageDao> sqlTemplate = new MessageSqlTemplate<>();
        QueryMessage queryMessage = new QueryMessage();
        List<MessageDao> topicList = sqlTemplate.queryMessage("Topic", queryMessage);
        dao.setListMessage(topicList);
        return dao;
    }


    @Subscribe
    @Override
    public void onSubScribeMessage(MessageManager msgManager) {
        super.onSubScribeMessage(msgManager);
    }

    @Override
    public void onClickItemMessage(MessageDao messageDao) {
        OnClickItemMessageListener onClickItemMessageListener =
                (OnClickItemMessageListener) getActivity();
        onClickItemMessageListener.onClickItemMessage(messageDao);
    }

}
