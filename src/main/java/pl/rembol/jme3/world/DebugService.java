package pl.rembol.jme3.world;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.rembol.jme3.world.hud.ConsoleLog;

import com.jme3.scene.Node;

@Component
public class DebugService {

	@Autowired
	private ConsoleLog log;

	@Autowired
	private Node rootNode;

	private int pathfinding = 0;

	private int display = 0;

	Node borderDisplayNode = new Node();

	Node clusterPathsNode = new Node();

	public void switchPathfinding() {
		pathfinding = (pathfinding + 1) % 5;

		String name;

		switch (pathfinding) {
		case 0:
			name = "Dummy mode";
			break;
		case 1:
			name = "Bresenham mode";
			break;
		case 2:
			name = "A-Star mode";
			break;
		case 3:
			name = "Hierarchical A-Star mode";
			break;
		case 4:
			name = "Hierarchical A-Star with lazy step building mode";
			break;
		default:
			name = "????";
			break;
		}

		log.addLine("Switched pathfinding to " + name);
	}

	public void switchDisplay() {
		display = (display + 1) % 3;

		String name;

		switch (display) {
		case 0:
			name = "no clusters";
			rootNode.detachChild(borderDisplayNode);
			rootNode.detachChild(clusterPathsNode);
			break;
		case 1:
			name = "cluster borders only";
			rootNode.attachChild(borderDisplayNode);
			break;
		case 2:
			name = "cluster borders and paths";
			rootNode.attachChild(clusterPathsNode);
			break;
		default:
			name = "????";
			break;
		}

		log.addLine("now displaying " + name);

	}

	public Node getBorderDisplayNode() {
		return borderDisplayNode;
	}

	public Node getClusterPathsNode() {
		return clusterPathsNode;
	}

	public int getPathfinding() {
		return pathfinding;
	}
}
