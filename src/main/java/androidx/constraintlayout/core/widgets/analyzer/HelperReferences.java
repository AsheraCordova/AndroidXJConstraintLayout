/*
 * Copyright (C) 2019 The Android Open Source Project
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
 */

package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.widgets.CoreBarrier;
import androidx.constraintlayout.core.widgets.ConstraintWidget;

class HelperReferences extends WidgetRun {
    public HelperReferences(ConstraintWidget widget) {
        super(widget);
    }

    @Override
    void clear() {
        runGroup = null;
        start.clear();
    }

    @Override
    void reset() {
        start.resolved = false;
    }

    @Override
    boolean supportsWrapComputation() {
        return false;
    }

    private void addDependency(DependencyNode node) {
        start.dependencies.add(node);
        node.targets.add(start);
    }

    @Override
    void apply() {
        if (widget instanceof CoreBarrier) {
            start.delegateToWidgetRun = true;
            CoreBarrier barrier = (CoreBarrier) widget;
            int type = barrier.getBarrierType();
            boolean allowsGoneWidget = barrier.getAllowsGoneWidget();
            switch (type) {
                case CoreBarrier.LEFT: {
                    start.type = DependencyNode.Type.LEFT;
                    for (int i = 0; i < barrier.mWidgetsCount; i++) {
                        ConstraintWidget refWidget = barrier.mWidgets[i];
                        if (!allowsGoneWidget && refWidget.getVisibility() == ConstraintWidget.GONE) {
                            continue;
                        }
                        DependencyNode target = refWidget.horizontalRun.start;
                        target.dependencies.add(start);
                        start.targets.add(target);
                        // FIXME -- if we move the DependencyNode directly in the ConstraintAnchor we'll be good.
                    }
                    addDependency(widget.horizontalRun.start);
                    addDependency(widget.horizontalRun.end);
                } break;
                case CoreBarrier.RIGHT: {
                    start.type = DependencyNode.Type.RIGHT;
                    for (int i = 0; i < barrier.mWidgetsCount; i++) {
                        ConstraintWidget refWidget = barrier.mWidgets[i];
                        if (!allowsGoneWidget && refWidget.getVisibility() == ConstraintWidget.GONE) {
                            continue;
                        }
                        DependencyNode target = refWidget.horizontalRun.end;
                        target.dependencies.add(start);
                        start.targets.add(target);
                        // FIXME -- if we move the DependencyNode directly in the ConstraintAnchor we'll be good.
                    }
                    addDependency(widget.horizontalRun.start);
                    addDependency(widget.horizontalRun.end);
                } break;
                case CoreBarrier.TOP: {
                    start.type = DependencyNode.Type.TOP;
                    for (int i = 0; i < barrier.mWidgetsCount; i++) {
                        ConstraintWidget refwidget = barrier.mWidgets[i];
                        if (!allowsGoneWidget && refwidget.getVisibility() == ConstraintWidget.GONE) {
                            continue;
                        }
                        DependencyNode target = refwidget.verticalRun.start;
                        target.dependencies.add(start);
                        start.targets.add(target);
                        // FIXME -- if we move the DependencyNode directly in the ConstraintAnchor we'll be good.
                    }
                    addDependency(widget.verticalRun.start);
                    addDependency(widget.verticalRun.end);
                } break;
                case CoreBarrier.BOTTOM: {
                    start.type = DependencyNode.Type.BOTTOM;
                    for (int i = 0; i < barrier.mWidgetsCount; i++) {
                        ConstraintWidget refwidget = barrier.mWidgets[i];
                        if (!allowsGoneWidget && refwidget.getVisibility() == ConstraintWidget.GONE) {
                            continue;
                        }
                        DependencyNode target = refwidget.verticalRun.end;
                        target.dependencies.add(start);
                        start.targets.add(target);
                        // FIXME -- if we move the DependencyNode directly in the ConstraintAnchor we'll be good.
                    }
                    addDependency(widget.verticalRun.start);
                    addDependency(widget.verticalRun.end);
                } break;
            }
        }
    }

    @Override
    public void update(Dependency dependency) {
        CoreBarrier barrier = (CoreBarrier) widget;
        int type = barrier.getBarrierType();

        int min = -1;
        int max = 0;
        for (DependencyNode node : start.targets) {
            int value = node.value;
            if (min == -1 || value < min) {
                min = value;
            }
            if (max < value) {
                max = value;
            }
        }
        if (type == CoreBarrier.LEFT || type == CoreBarrier.TOP) {
            start.resolve(min + barrier.getMargin());
        } else {
            start.resolve(max + barrier.getMargin());
        }
    }

    public void applyToWidget() {
        if (widget instanceof CoreBarrier) {
            CoreBarrier barrier = (CoreBarrier) widget;
            int type = barrier.getBarrierType();
            if (type == CoreBarrier.LEFT
                    || type == CoreBarrier.RIGHT) {
                widget.setX(start.value);
            } else {
                widget.setY(start.value);
            }
        }
    }
}
