/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.DD_20131001_DG;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.org.omg.spec.CMMN_20151109_DC.Color;

@XStreamAlias("Style")
public class Style {

    @XStreamAsAttribute
    private String fontName;

    @XStreamAlias("FontColor")
    private Color fontColor;

    @XStreamAsAttribute
    private Double fontSize;

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Double getFontSize() {
        return fontSize;
    }

    public void setFontSize(Double fontSize) {
        this.fontSize = fontSize;
    }
}
