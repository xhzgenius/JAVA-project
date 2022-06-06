package ui;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import logic.Fellow;
import animation.api.*;
import animation.api.Timeline.*;
import animation.api.callback.TimelineCallbackAdapter;
import animation.api.ease.*;

public class ContainerTeam extends JPanel {
    private Box box;
    private JPanel canvas;
    private JFrame frame;

    ArrayList<Fellow> fellows;
    HashMap<Fellow, TeamMember> mapMembers;

    ContainerTeam opponent;

    private Runnable propagateRerender = () -> {};

    ContainerTeam(JPanel canvas, JFrame frame) {
        super();
        box = new Box(BoxLayout.X_AXIS);
        super.add(box);
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());

        this.canvas = canvas;
        this.frame = frame;
        this.mapMembers = new HashMap<>();
    }

    public void setPropagateRerender(Runnable propagateRerender) {
        this.propagateRerender = propagateRerender;
    }

    public void setOpponent(ContainerTeam opponent) {
        this.opponent = opponent;
    }

    public void render(ArrayList<Fellow> fellows) {
        this.clear();
        this.fellows = fellows;
        for (Fellow fellow : fellows) {
            registerOne(fellow);
        }
    }

    private void registerOne(Fellow fellow) {
        ComponentCardFellow card = new ComponentCardFellow(fellow);
        TeamMember member = new TeamMember(card);
        mapMembers.put(fellow, member);
        box.add(member, box.getComponentCount() - 1);
    }

    public void attack(int attackerIndex, int defenderIndex, Runnable then) {
        Fellow attacker = fellows.get(attackerIndex);
        Fellow defender = opponent.fellows.get(defenderIndex);
        TeamMember attackerMember = mapMembers.get(attacker);
        TeamMember defenderMember = opponent.mapMembers.get(defender);

        System.out.println(attacker.Name + " attacks " + defender.Name);

        ComponentCardFellow attackerCard = attackerMember.eject(canvas, frame);
        // attackerCard.setLocation(defenderLocation);
        Timeline.builder(attackerCard)
            .addPropertyToInterpolate("location", attackerCard.getLocation(), CanvasUtil.getRelativeTopLeft(defenderMember, canvas))
            .setDuration(3000)
            .setEase(new Spline(0.75f))
            .addCallback(new TimelineCallbackAdapter() {
                @Override
                public void onTimelineStateChanged(TimelineState oldState, TimelineState newState, float durationFraction, float timelinePosition) {
                    if (newState == TimelineState.DONE) {
                        UIUtil.tryInvokeAndWait(() -> canvas.remove(attackerCard));
                        UIUtil.tryInvokeAndWait(() -> propagateRerender.run());
                        UIUtil.tryInvokeAndWait(() -> then.run());
                    }
                }
            })
            .playLoop(2, RepeatBehavior.REVERSE);
    }

    public void clear() {
        this.fellows = null;
        mapMembers.clear();
        box.removeAll();
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalGlue());
    }
}

class TeamMember extends JPanel {
    ComponentCardFellow card;
    Placeholder placeholder;

    TeamMember(ComponentCardFellow card) {
        super();
        this.card = card;
        add(card);
    }

    public ComponentCardFellow eject(JPanel canvas, JFrame frame) {
        Rectangle rect = new Rectangle(0, 0, 96, 128);
        this.placeholder = new Placeholder(rect);
        CanvasUtil.promote(canvas, frame, card);
        add(placeholder);
        return card;
    }

    public void emplace(ComponentCardFellow card) {
        remove(placeholder);
        this.card = card;
        add(card);
    }
}

class Placeholder extends JPanel {
    Placeholder(Rectangle rect) {
        super();
        Dimension dim = new Dimension(rect.width, rect.height);
        this.setPreferredSize(dim);
        this.setMaximumSize(dim);
        this.setMinimumSize(dim);
    }
}
