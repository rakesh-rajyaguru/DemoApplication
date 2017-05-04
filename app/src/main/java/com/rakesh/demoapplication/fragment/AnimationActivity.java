package com.rakesh.demoapplication.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.adapter.EffectAdapter;
import com.rakesh.demoapplication.animator.Techniques;
import com.rakesh.demoapplication.animator.YoYo;

public class AnimationActivity extends Fragment {

    private View mTarget;
    private ListView mListView;
    private EffectAdapter mAdapter;
    private YoYo.YoYoString rope;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_animation_fragment,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity=getActivity();
        initComponent();
    }

    private void initComponent() {
        if(getView()==null){
            return;
        }
        mTarget = getView().findViewById(R.id.hello_world);
        mListView = (ListView)getView().findViewById(R.id.list);
        mAdapter = new EffectAdapter(mActivity);
        mListView.setAdapter(mAdapter);

        rope = YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTarget);// after start,just click mTarget view, rope is not init

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Techniques technique = (Techniques)view.getTag();
                rope =  YoYo.with(technique).duration(1200)
                        .interpolate(new AccelerateDecelerateInterpolator())
                        .listen(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationCancel(Animator animation) {
                                Toast.makeText(mActivity, "canceled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .playOn(mTarget);
            }
        });

        mTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rope != null) {
                    rope.stop(true);
                }
            }
        });
    }



}
