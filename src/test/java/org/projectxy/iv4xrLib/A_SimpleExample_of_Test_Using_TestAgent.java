package org.projectxy.iv4xrLib;

import static nl.uu.cs.aplib.AplibEDSL.SEQ;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import eu.iv4xr.framework.mainConcepts.TestAgent;
import eu.iv4xr.framework.mainConcepts.TestDataCollector;
import eu.iv4xr.framework.mainConcepts.W3DAgentState;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import nl.uu.cs.aplib.mainConcepts.GoalStructure;

/**
 * Contain one Junit test showing how to create a test agent, specify a simple testing task,
 * run the agent, and then check the resulting test verdicts.
 */
public class A_SimpleExample_of_Test_Using_TestAgent {

	@Test
	public void test1() throws InterruptedException {

		// Create an instance of interface to the SUT. You also need to somehow deploy the
		// SUT, since this interface will now need to be able to talk to it:
		MyEnv theEnv = new MyEnv();

		// create a data collector where we will be logging test verdicts:
		var dataCollector = new TestDataCollector();

		// i cheat a bit by exposing the agent state. Let's create it:
		W3DAgentState myAgentState = new W3DAgentState();

		// create a test agent; attach state, environment, and data-collector to it:

		var testAgent = new TestAgent("agent0","some role name, else nothing")

				. attachState(myAgentState)
				. attachEnvironment(theEnv)
				. setTestDataCollector(dataCollector);

		// let's define a simple testing task:
		//  (1) interact with some in-SUT entity called "button0"
		//  (2) check that this causes "door1" to open. If so, log a success-verdict, and else
		//      a fail-verdict:

		GoalStructure testingTask = SEQ(

				GoalLib.entityInteracted("button0"),

				GoalLib.entityInvariantChecked(testAgent,
						"door1", 
						(WorldEntity e) -> e.getBooleanProperty("isOpen") == true));

		// assign the task to the agent:

		testAgent . setGoal(testingTask);

		// run the agent in a simple loop:
		int i=0;
		while (testingTask.getStatus().inProgress()) {
			System.out.println("*** " + i + ", " + myAgentState.wom.agentId + " @" + myAgentState.wom.position);
			Thread.sleep(50);
			i++;
			testAgent.update();
			if (i>200) {
				break;
			}
		}

		// we can print the goal structure's status, if you are curious:
		testingTask.printGoalStructureStatus();

		// we have a single check in the above testing task; check if the verdict is a pass:
		assertTrue(dataCollector.getNumberOfPassVerdictsSeen() == 1);

	}

}
