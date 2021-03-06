package org.projectxy.iv4xrLib;

import static eu.iv4xr.framework.Iv4xrEDSL.testgoal;
import static nl.uu.cs.aplib.AplibEDSL.ABORT;
import static nl.uu.cs.aplib.AplibEDSL.FIRSTof;
import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static nl.uu.cs.aplib.AplibEDSL.goal;

import java.util.function.Predicate;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.W3DAgentState;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.mainConcepts.ObservationEvent.VerdictEvent;
import eu.iv4xr.framework.spatial.Vec3;
import nl.uu.cs.aplib.mainConcepts.Goal;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;


/**
 * A set of three typical goals for testing an SUT using an iv4XR agent. First let's go 
 * through some terminologies.
 * 
 *   (1) a goal represents some SUT state that the agent wants. To be in that state, it needs
 *   to drive the SUT. To do this, a "solver" is needed. This is called "tactic". Every goal
 *   will thus need a tactic. When we define a goal, we should therefore also specify what
 *   the tactic we want to use for solving it.
 *   
 *   (2) We can structure multiple goals structurally, e.g. if we want to achieve all of them,
 *   but in a certain order. A hierarchically structured goals is called a "goal structure".
 *   
 *   (3) So below we actually provide a set of typical "goal structures". Again, for each goal
 *   in the structure, we will have to specify the corresponding tactic.
 *   
 *   (4) The provided goal-structures are also "parameterized"! So, although below we have to
 *   invest effort to define the goal-structures, after that we can use them endlessly to
 *   solve various instances.
 *   
 * We give examples of three typical goal-structure:
 * 
 *   (1) entityInCloseRange(e,epsilon) : to steer the test-agent towards the in-SUT entity e,
 *                                       to within the distance epsilon.
 *                                       
 *   (2) entityInteracted(e) : to steer the test-agent to get close enough to the entity e, and
 *                             then to interact with it.
 *                             
 *   (3) entityInvariantChecked(a,e,p) : to steer the test-agent a to get close enough to the entity e
 *                             and then to check whether the predicate p(e) holds. This counts as
 *                             "checking". If the predicate holds, a "pass" verdict will be logged,
 *                             and else a "fail" verdict will be logged.                       
 *                             
 *  A template implementation is provided, which assumes the existence of some tactics. The
 *  tactics can be expected to be rather SUT dependent, so we do not provide much sample code
 *  for them. They can be found in the class TacticLib.                                                             
 */

public class GoalLib {

	/**
	 * This method will construct a goal (more precisely: a goal structure) that will drive
	 * your agent to get close to the given entity (up to some distance specified by
	 * epsilon).
	 * 
	 * The agent will fail the goal if it no longer believes the entity is reachable.
	 */
	public static GoalStructure entityInCloseRange(String entityId, float epsilon) {

		//define the goal, namely that the agent position should be close enough to the target entity

		Goal goal = // create a goal, give it some name:

				new Goal("This entity is closeby: " + entityId)

				// the predicate to solve:

				. toSolve((W3DAgentState belief) -> {
					// get the entity:
					WorldEntity e = belief.wom.getElement(entityId);
					if (e == null) return false;
					// calculate the distance of the agent towards e:
					float distance = Vec3.dist(belief.wom.position,e.position);
					// ok if the distance is close enough:
					return distance <= epsilon; // distance is less than some epsilon
				})

				// specify the tactic to be used to solve the goal. Below we say: 
				//   (1) navigate directly to the entity
				//   (2) but if the entity is not known yet, then explore the world first
				//   (3) if none of the above is applicable we run out of idea and abort the goal

				. withTactic(
						FIRSTof(
								TacticLib.navigateToEntity(entityId),//move to the goal position
								TacticLib.explore(), //explore if the goal position is unknown
								ABORT()));

		//the above is a "goal", we need to return a goal-structure. We can just lift it:
		return goal. lift();
	}



	/**
	 * Construct a goal structure that will make an agent to move towards the given entity,
	 * until it is in the interaction-distance with the entity; and then interacts with it.
	 */
	public static GoalStructure entityInteracted(String entityId) {

		float interactionDistance = 0.5f; // specify whatever the interaction distance is...

		// the goal for the interaction part:
		Goal interacton = 

				// create a goal, give it a name etc:

				goal(String.format("This entity is interacted: [%s]", entityId))

				// specify the predicate to solve. In this case we don't want to solve anything.
				// we will put the solving tactic instead.
				. toSolve((W3DAgentState belief) -> true) 

				// Specify the solving tactic: we interact, if interaction is some how not possible,
				// the goal as failed by invoking ABORT:

				. withTactic(
						FIRSTof( 
								TacticLib.interact(entityId),// interact with the entity
								ABORT()))   // abort if we can't interact
				;

		// the final goal structure is a composition of moving close to the entity, and then interacting with it:

		return SEQ(entityInCloseRange(entityId,interactionDistance), interacton.lift());
	}

	/**
	 * Create a test-goal to check the state of an in-game entity, whether it satisfies the given predicate.
	 */
	public static GoalStructure entityInvariantChecked(TestAgent agent, String entityId, Predicate<WorldEntity> predicate){

		float epsilon = 1; // specify some distance here, that should be close enough for the agent to observe
		// the state of an in-SUT entity

		// we'll specify the checking here:
		Goal invariantchecking  = 

				// create a goal, give it a name etc:

				testgoal("Invariant check " + entityId, agent)

				// the goal predicate to solve. For this we don't want to solve anything. Instead, we want
				// to check the SUT current state. So, the goal is just "true":

				. toSolve((W3DAgentState belief) -> true) // nothing to solve

				// implement the check:
				. invariant(agent,                        // something to check :)
						(W3DAgentState belief) -> {
							// get the entity:
							WorldEntity e = belief.wom.getElement(entityId);
							if (e != null && predicate.test(e)) 
								// if the check is passed, return a "pass" verdict:
								return new VerdictEvent("Object-check " + entityId, "", true);
							else 
								// else return a "fail" verdict:
								return new VerdictEvent("Object-check " + entityId, "", false);

						})
				.withTactic(TacticLib.observe())
				;

		// the final goal is a composition of first getting close to the entity, and then checking its state:
		return SEQ(entityInCloseRange(entityId,epsilon), invariantchecking.lift());
	}

}
