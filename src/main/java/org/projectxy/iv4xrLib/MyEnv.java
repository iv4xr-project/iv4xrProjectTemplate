package org.projectxy.iv4xrLib;

import eu.iv4xr.framework.exception.Iv4xrError;
import eu.iv4xr.framework.mainConcepts.W3DEnvironment;
import eu.iv4xr.framework.mainConcepts.WorldModel;
import eu.iv4xr.framework.spatial.Vec3;
import eu.iv4xr.framework.spatial.meshes.Mesh;
import nl.uu.cs.aplib.mainConcepts.Environment.EnvOperation;
import nl.uu.cs.aplib.utils.Pair;

/**
 * This class provides the interface between iv4XR test agents and your System
 * Under Test (SUT). This interface provides methods that allow agents to
 * control the SUT and to obtain information about the SUT state. You have to
 * implement at least the methods listed below.
 * 
 * A word of caution first: keep in mind that an iv4XR agent is often used to
 * control an agent that lives in the world of your SUT. We will call the second
 * agent in-SUT agent. Your iv4XR agent X, might instruct an in-SUT agent A to
 * interact with another in-SUT agent B. I hope this is a bit clear :)
 * 
 * (1) The primary method you should implement is the method sendCommand_(cmd).
 * However, your iv4XR agent X will prefer to control the SUT through more
 * abstract methods, rather than by directly invoking sendCommand_(). For this
 * reason below we give some examples of such more-abstract methods such as
 * move() and interact().
 * 
 * (2) The agent's runtime assumes you provide the method observe() ... see
 * below. So, you should have this method.
 * 
 */
public class MyEnv extends W3DEnvironment {

    /**
     * In principle this is the primary method you need to implement. This method is
     * the one that will send a command to the SUT and reports back the state of the
     * SUT.
     * 
     * IMPORRTANT: the method should return an instance of WorldModel
     * (eu.iv4xr.framework.mainConcepts.WorldModel). The SUT might send something
     * else; but this method sendCommand_() should then transform it into an
     * instance of WorldModel. It can be literally an instance of WorldModel, or a
     * subclass of it if you have created your own subclass to enrich it.
     * 
     * The method sendCommand_() sends a command, which is represented as an
     * instance of the class EnvOperation
     * (nl.uu.cs.aplib.mainConcepts.Environment.EnvOperation).
     * 
     * To create a command we can use the constructor of EnvOperation:
     * 
     * new EnvOperation(String invokerId, String targetId, String command, Object
     * arg, Class expectedTypeOfResult)
     * 
     * invokerId: the id/name of the agent at the SUT-side which we want to command.
     * command: the command for the agent meant by invokerId to do something (e.g.
     * to move, or to interact with something else) targetId: if the command is a
     * command like "interact" it usually also involves the target of the
     * interaction (e.g. imagine a command for agent A in the SUT to open a door in
     * the SUT; A would be invokerId, and the door would be the target).
     * 
     * arg: a single object that is the parameter of the command. For example a
     * command like "move" might require a direction to be specified as a parameter.
     * Only one parameter is accomodated in this protocol. If you need multiple
     * parameters, you should wrap it in a single object and deal with packing and
     * unpacking.
     * 
     * expectedTypeOfResult: can be ignored for now.
     * 
     * Examples:
     * 
     * new EnvOperation("agent0",null,"Observe",null,Dontcare.class) new
     * EnvOperation("agent0","button1","Interact",null,Dontcare.class)
     * 
     */
    protected Object sendCommand_(EnvOperation cmd) {

        // finish this implementation

        Object RawSUTstate; // in the switch below, put the value sent back by the SUT in this variable

        switch (cmd.command) {
        case "Observe":
            /* send this command to the SUT, wait for its response and repackage it */;
            break;
        case "Move":
            /* send this command to the SUT, wait for its response and repackage it */;
            break;
        case "Interact":
            /* send this command to the SUT, wait for its response and repackage it */;
            break;
        case "LoadWorld":
            /* send this command to the SUT, wait for its response and repackage it */;
            break;
        }
        // at this point the variable RawSUTstate should contain a representation of the
        // SUT-state,
        // as sent back by the SUT through the above command.
        // Next, we need to either cast it, or somehow post-process it to an instance of
        // WorldModel:

        WorldModel SUTstate = null; // = postProcess(RawSUTstate)

        return SUTstate;
    }

    /**
     * Send a command to the SUT to to send back its state. You should have this
     * method, as the agent's framework automatically invoke it at every update
     * cycle.
     * 
     * The effect of this method is equivalent to calling:
     * 
     * sendCommand_(new EnvOperation(agentId,null,"Observe",null,Dontcare.class)
     * 
     * 
     * the result is cast to WorldModel
     * (eu.iv4xr.framework.mainConcepts.WorldModel).
     * 
     * In principle you don't need to redefine this method as long as you define the
     * corresponding logic in sendCommand_().
     */
    @Override
    public WorldModel observe(String agentId) {
        return (WorldModel) sendCommand(agentId, null, "Observe", null, WorldModel.class);
    }

    /**
     * Command the in-SUT agentId to interact on another in-SURT entity identified
     * by targetId. This method pass the call to sendCommand(..) from an ancestor
     * class, which in turn will call your sendCommand_() above. The effect is
     * equivalent to calling:
     * 
     * sendCommand_(new
     * EnvOperation(agentId,targetId,"Interact",null,Dontcare.class)
     * 
     * 
     * the result of sendCommand_ is then cast to WorldModel
     * (eu.iv4xr.framework.mainConcepts.WorldModel).
     * 
     * In principle you don't need to redefine this method as long as you define the
     * corresponding logic in sendCommand_().
     */
    public WorldModel interact(String agentId, String targetId) {
        return (WorldModel) sendCommand(agentId, targetId, "Interact", null, WorldModel.class);
    }

    /**
     * A command to instruct the in-SUT agentId to move a small distance towards the
     * given target location. The method pass the call to sendCommand(..) from an
     * ancestor class, which in turn will call your sendCommand_() above. The effect
     * is equivalent to calling:
     * 
     * sendCommand_(new EnvOperation(agentId, null, "Move",
     * Pair(agentLocation,targetLocation), Dontcare.class)
     * 
     * 
     * the result is cast to WorldModel
     * (eu.iv4xr.framework.mainConcepts.WorldModel).
     * 
     * In principle you don't need to redefine this method as long as you define the
     * corresponding logic in sendCommand_().
     */
    @Override
    public WorldModel moveToward(String agentId, Vec3 agentLocation, Vec3 targetLocation) {
        return (WorldModel) sendCommand(agentId, null, "Move", new Pair(agentLocation, targetLocation),
                WorldModel.class);
    }

    /**
     * Send a command to the real environment that should cause it to send over the
     * navigation-mesh of its 3D world (well, it has it). This mesh is assumed to be
     * static (does not change through out the agents' runs).
     * 
     * In principle you don't need to redefine this method as long as you define the
     * corresponding logic in sendCommand_().
     */
    public void loadWorld() {
        worldNavigableMesh = (Mesh) sendCommand(null, null, "LoadWorld", null, Mesh.class);
        if (worldNavigableMesh == null)
            throw new Iv4xrError("Fail to load the navgation-mesh of the world");
    }

}
