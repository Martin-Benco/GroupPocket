package sk.spsepo.grouppocket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import sk.spsepo.grouppocket.data.AccountManager;
import sk.spsepo.grouppocket.data.GroupStorage;
import sk.spsepo.grouppocket.data.Group;

public class GroupChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);

        // Získaj aktuálnu skupinu
        String groupName = getActivity().getIntent().getStringExtra("groupName");
        List<Group> groups = GroupStorage.loadGroups(getContext());
        Group currentGroup = null;
        
        for (Group group : groups) {
            if (group.getName().equals(groupName)) {
                currentGroup = group;
                break;
            }
        }

        // Inicializácia správ (simulácia)
        messages = new ArrayList<>();
        if (currentGroup != null && currentGroup.getMembers().size() > 1) {
            // Vyber členov okrem seba pre simulované správy
            List<String> otherMembers = new ArrayList<>(currentGroup.getMembers());
            otherMembers.remove(AccountManager.getCurrentUserEmail());
            
            if (!otherMembers.isEmpty()) {
                // Vyber len jedného člena (nie admina) pre všetky správy
                String memberSender = otherMembers.get(otherMembers.size() - 1); // Berieme posledného člena (typicky nie admin)
                
                // Všetky správy sú od toho istého člena
                messages.add(new Message(memberSender, "Hey everyone! I just added a new expense."));
                messages.add(new Message(memberSender, "Please check it and confirm if it's correct."));
                messages.add(new Message(memberSender, "I split it equally among all members."));
            }
        }

        // Nastavenie RecyclerView
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // Nastavenie odoslania správy
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                messages.add(new Message(AccountManager.getCurrentUserEmail(), messageText));
                chatAdapter.notifyItemInserted(messages.size() - 1);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
                messageInput.setText("");
            }
        });

        return view;
    }

    private static class Message {
        String sender;
        String text;

        Message(String sender, String text) {
            this.sender = sender;
            this.text = text;
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
        private List<Message> messages;

        ChatAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, int position) {
            Message message = messages.get(position);
            // Ak je správa od aktuálneho používateľa, nezobrazuj email
            if (message.sender.equals(AccountManager.getCurrentUserEmail())) {
                holder.senderName.setVisibility(View.GONE);
            } else {
                holder.senderName.setVisibility(View.VISIBLE);
                holder.senderName.setText(message.sender);
            }
            holder.messageText.setText(message.text);

            // Ak je správa od aktuálneho používateľa, zarovnaj ju vpravo
            if (message.sender.equals(AccountManager.getCurrentUserEmail())) {
                holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                holder.messageText.setBackgroundResource(R.drawable.message_background_sent);
            } else {
                holder.itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                holder.messageText.setBackgroundResource(R.drawable.message_background);
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class MessageViewHolder extends RecyclerView.ViewHolder {
            TextView senderName, messageText;

            MessageViewHolder(View itemView) {
                super(itemView);
                senderName = itemView.findViewById(R.id.senderName);
                messageText = itemView.findViewById(R.id.messageText);
            }
        }
    }
} 