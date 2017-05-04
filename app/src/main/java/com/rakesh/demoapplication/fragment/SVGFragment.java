/*
 * Copyright (C) 2016 Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rakesh.demoapplication.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jrummyapps.android.widget.AnimatedSvgView;
import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.animator.SVG;

public class SVGFragment extends Fragment {

    private AnimatedSvgView svgView;
    private Button btn_previous, btn_next;
    private int index = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_svg_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getView() == null) {
            return;
        }
        svgView = (AnimatedSvgView) getView().findViewById(R.id.animated_svg_view);
        btn_previous = (Button) getView().findViewById(R.id.btn_previous);
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPrevious();
            }
        });
        btn_next = (Button) getView().findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNext();
            }

        });
        svgView.postDelayed(new Runnable() {

            @Override
            public void run() {
                svgView.start();
            }
        }, 500);

        svgView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (svgView.getState() == AnimatedSvgView.STATE_FINISHED) {
                    svgView.start();
                }
            }
        });

        svgView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {

            @Override
            public void onStateChange(int state) {
                if (state == AnimatedSvgView.STATE_TRACE_STARTED) {
                    getView().findViewById(R.id.btn_previous).setEnabled(false);
                    getView().findViewById(R.id.btn_next).setEnabled(false);
                } else if (state == AnimatedSvgView.STATE_FINISHED) {
                    getView().findViewById(R.id.btn_previous).setEnabled(index != -1);
                    getView().findViewById(R.id.btn_next).setEnabled(true);
                    if (index == -1) index = 0; // first time
                }
            }
        });
    }


    public void onNext() {
        if (++index >= SVG.values().length) index = 0;
        setSvg(SVG.values()[index]);
    }

    public void onPrevious() {
        if (--index < 0) index = SVG.values().length - 1;
        setSvg(SVG.values()[index]);
    }

    private void setSvg(SVG svg) {
        svgView.setGlyphStrings(svg.glyphs);
        svgView.setFillColors(svg.colors);
        svgView.setViewportSize(svg.width, svg.height);
        svgView.setTraceResidueColor(0x32000000);
        svgView.setTraceColors(svg.colors);
        svgView.rebuildGlyphData();
        svgView.start();
    }

}
