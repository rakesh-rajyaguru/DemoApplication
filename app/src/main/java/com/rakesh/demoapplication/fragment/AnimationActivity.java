package com.rakesh.demoapplication.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
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
        return inflater.inflate(R.layout.layout_animation_fragment, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        initComponent();
        Log.e(getClass().getSimpleName(), "onActivity Created");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(getClass().getSimpleName(), "onActivity Result");
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        Log.e(getClass().getSimpleName(), "onAttachFragment");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(getClass().getSimpleName(), "onCreateView");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(getClass().getSimpleName(), "on Attach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(getClass().getSimpleName(), "on Detach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(getClass().getSimpleName(), "onHiden change:" + hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(getClass().getSimpleName(), "Destroy View");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.e(getClass().getSimpleName(), "Option menu created");
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.e(getClass().getSimpleName(), "Option menu prepared");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(getClass().getSimpleName(), "Option item selected ");
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        Log.e(getClass().getSimpleName(), "Option menu destroy ");
    }

    private void initComponent() {
        if (getView() == null) {
            return;
        }
        mTarget = getView().findViewById(R.id.hello_world);
        mListView = (ListView) getView().findViewById(R.id.list);
        mAdapter = new EffectAdapter(mActivity);
        mListView.setAdapter(mAdapter);

        rope = YoYo.with(Techniques.FadeIn).duration(1000).playOn(mTarget);// after start,just click mTarget view, rope is not init

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Techniques technique = (Techniques) view.getTag();
            rope = YoYo.with(technique).duration(1200)
                    .interpolate(new AccelerateDecelerateInterpolator())
                    .listen(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            Toast.makeText(mActivity, "canceled", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .playOn(mTarget);
        });

        mTarget.setOnClickListener(v -> {
            if (rope != null) {
                rope.stop(true);
            }
        });
    }


}
