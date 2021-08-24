package fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.groupchat.GroupChat;
import com.example.groupchat.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupFragment extends Fragment {

  private   View groupFragmentView;
    private ListView listView;
    private   ArrayAdapter<String> arrayAdapter;
    private  ArrayList<String> list_of_group=new ArrayList<>();
    private  DatabaseReference  GroupRef;


    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView=inflater.inflate(R.layout.fragment_group,container,false);
        GroupRef= FirebaseDatabase.getInstance().getReference().child("Groups");

                IntializeFields();
                RetriveAndDisplayGroup();

     listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
           String currentgroupName=adapterView.getItemAtPosition(position).toString();
             Intent intent=new Intent(getContext(), GroupChat.class);
            intent.putExtra("groupName",currentgroupName);
             startActivity(intent);
         }
     });
        return groupFragmentView;
    }

    private void RetriveAndDisplayGroup() {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String>set=new HashSet<>();
                Iterator iterator=snapshot.getChildren().iterator();
                while ((iterator.hasNext())){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_group.clear();
                list_of_group.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void IntializeFields() {
        listView=groupFragmentView.findViewById(R.id.list_view);
        arrayAdapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list_of_group);
        listView.setAdapter(arrayAdapter);
    }
}