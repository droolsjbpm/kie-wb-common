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

package org.kie.workbench.common.dmn.client.widgets.grid;

import com.ait.lienzo.client.core.shape.Line;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.ait.lienzo.shared.core.types.TextUnit;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ExpressionEditorColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.NameColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DescriptionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.OutputClauseColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.relation.RelationColumn;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.themes.GridRendererTheme;

public class BaseExpressionGridTheme implements GridRendererTheme {

    public static final String BACKGROUND_FILL_COLOUR = "#c7ffca";

    public static final String LABEL_BACKGROUND_FILL_COLOUR = "#c7ffca";

    public static final String HOVER_STATE_STROKE_COLOUR = "#008acd";

    public static final String GRID_STROKE_COLOUR = "#dbdbdb";

    public static final String ROW_NUMBER_BACKGROUND_FILL_COLOUR = "#c7ffca";

    public static final String INPUT_CLAUSE_BACKGROUND_FILL_COLOUR = "#ddffdf";

    public static final String OUTPUT_CLAUSE_BACKGROUND_FILL_COLOUR = "#e9ffea";

    public static final String DESCRIPTION_COLUMN_BACKGROUND_FILL_COLOUR = "#f7f7f7";

    public static final String EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR = "#f7f7f7";

    public static final String UNDEFINED_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR = "#f7f7f7";

    public static final String LITERAL_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR = "#f7f7f7";

    public static final String RELATION_BACKGROUND_FILL_COLOUR = "#f7f7f7";

    public static final String FONT_FAMILY_HEADER = "Open Sans, Helvetica, Arial, sans-serif";

    public static final String FONT_FAMILY_LABEL = "Open Sans, Helvetica, Arial, sans-serif";

    public static final String FONT_FAMILY_EXPRESSION = "Courier New";

    public static final int FONT_SIZE = 10;

    @Override
    public String getName() {
        return "DMN Editor";
    }

    @Override
    public MultiPath getSelector() {
        return new MultiPath().setVisible(false);
    }

    @Override
    public Rectangle getCellSelectorBorder() {
        return new Rectangle(0, 0).setStrokeColor(HOVER_STATE_STROKE_COLOUR).setStrokeWidth(2.0);
    }

    @Override
    public Rectangle getCellSelectorBackground() {
        return new Rectangle(0, 0).setVisible(false);
    }

    @Override
    public Rectangle getHeaderBackground(final GridColumn<?> column) {
        return new Rectangle(0, 0)
                .setFillColor(BACKGROUND_FILL_COLOUR);
    }

    @Override
    public Rectangle getHeaderLinkBackground(final GridColumn<?> column) {
        return new Rectangle(0, 0)
                .setFillColor(ColorName.LIGHTGRAY);
    }

    @Override
    public MultiPath getHeaderGridLine() {
        return new MultiPath()
                .setStrokeColor(GRID_STROKE_COLOUR)
                .setStrokeWidth(1.0)
                .setVisible(true);
    }

    @Override
    public Text getHeaderText() {
        return new Text("")
                .setFillColor(ColorName.BLACK)
                .setFontSize(FONT_SIZE)
                .setFontFamily(FONT_FAMILY_HEADER)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    @Override
    public Rectangle getBodyBackground(final GridColumn<?> column) {
        final Rectangle background = new Rectangle(0, 0);
        if (column instanceof NameColumn) {
            background.setFillColor(LABEL_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.NameColumn) {
            background.setFillColor(LABEL_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.NameColumn) {
            background.setFillColor(LABEL_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof RelationColumn) {
            background.setFillColor(RELATION_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof LiteralExpressionColumn) {
            background.setFillColor(LITERAL_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof RowNumberColumn) {
            background.setFillColor(ROW_NUMBER_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof InputClauseColumn) {
            background.setFillColor(INPUT_CLAUSE_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof OutputClauseColumn) {
            background.setFillColor(OUTPUT_CLAUSE_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof DescriptionColumn) {
            background.setFillColor(DESCRIPTION_COLUMN_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof ExpressionEditorColumn) {
            background.setFillColor(EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR);
        } else if (column instanceof UndefinedExpressionColumn) {
            background.setFillColor(UNDEFINED_EXPRESSION_COLUMN_BACKGROUND_FILL_COLOUR);
        }
        return background;
    }

    @Override
    public MultiPath getBodyGridLine() {
        return new MultiPath()
                .setStrokeColor(GRID_STROKE_COLOUR)
                .setStrokeWidth(1.0)
                .setVisible(true);
    }

    @Override
    public Text getBodyText() {
        return new Text("")
                .setFillColor(ColorName.BLACK)
                .setFontSize(FONT_SIZE)
                .setFontFamily(FONT_FAMILY_LABEL)
                .setTextUnit(TextUnit.PT)
                .setListening(false)
                .setTextBaseLine(TextBaseLine.MIDDLE)
                .setTextAlign(TextAlign.CENTER);
    }

    @Override
    public Rectangle getGridBoundary() {
        return new Rectangle(0, 0)
                .setStrokeColor(GRID_STROKE_COLOUR)
                .setStrokeWidth(1.0)
                .setVisible(true);
    }

    @Override
    public Line getGridHeaderBodyDivider() {
        return new Line()
                .setStrokeColor(GRID_STROKE_COLOUR)
                .setStrokeWidth(1.0)
                .setVisible(true);
    }
}
