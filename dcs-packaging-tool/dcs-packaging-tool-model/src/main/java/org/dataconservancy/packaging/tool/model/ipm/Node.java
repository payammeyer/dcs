package org.dataconservancy.packaging.tool.model.ipm;

import org.dataconservancy.packaging.tool.model.dprofile.NodeType;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A node in the PTG tree which the user views and manipulates. The tree is a
 * DAG. The tree must contain at least one node, the root node.
 * 
 * Each node is associated with a domain object, file data, and a type. The type
 * of the node constrains its position in the tree and explains how it is mapped
 * to a type of domain object.
 * 
 * 
 */
public class Node {

    private URI identifier;
    private Node parent;
    private List<Node> children;
    private URI domainObject;
    private FileInfo fileInfo;
    private NodeType nodeType;
    private boolean ignored;
    private List<NodeType> subTypes;

    /**
     * Identifier is required to be unique for each node.
     * @param identifier A unique identifier representing the node
     */
    public Node(URI identifier) {
        this.identifier = identifier;
    }

    /**
     * @return Unique identifier of the node in the tree.
     */
    public URI getIdentifier() {
        return identifier;
    }

    /**
     * Set unique identifier of the node in the tree.
     * 
     * @param id the new identifier for the node
     */
    public void setIdentifier(URI id) {
        this.identifier = id;
    }

    /**
     * @return Parent node or null if node has no parent.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Set the parent node.
     * 
     * @param parent The new parent of the node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return Children of node as a list that should not be modified, or null if no children have been added.
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * Add a child node. The parent of the child node will be set to this node.
     * 
     * @param node The new child to add to the node.
     */
    public void addChild(Node node) {
        if (children == null) {
            children = new ArrayList<>();
        }

        children.add(node);
        node.setParent(this);
    }

    /**
     * Remove a child node and set its parent to null.
     * 
     * @param node The child node to remove from the node.
     */
    public void removeChild(Node node) {
        if (children != null) {
            children.remove(node);
        }
        
        node.setParent(null);
    }

    /**
     * Sets the list of children for this node.
     * @param children The children to add for the node.
     */
    public void setChildren(List<Node> children) {
        this.children = children;
    }

    /**
     * @return Identifier of domain object associated with node.
     */
    public URI getDomainObject() {
        return domainObject;
    }

    /**
     * Set identifier of domain object associated with node.
     * 
     * @param id The id of the domain object corresponding to this node.
     */
    public void setDomainObject(URI id) {
        this.domainObject = id;
    }

    /**
     * @return Information about the file associated with the node.
     */
    public FileInfo getFileInfo() {
        return fileInfo;
    }

    /**
     * Set information about file associated with node.
     * 
     * @param info The FileInfo associated with this node.
     */
   public void setFileInfo(FileInfo info) {
       this.fileInfo = info;
   }

    /**
     * @return The primary(structural) type of the node.
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * Set the type of the node.
     * 
     * @param type The node type object that represents the primary(structural) node type.
     */
    public void setNodeType(NodeType type) {
        this.nodeType = type;
    }

    /**
     * Add a new sub type to the node. This type will not affect the structural type of the node.
     * @param subNodeType The node type to add to the node.
     */
    public void addSubNodeType(NodeType subNodeType) {
        if (subTypes == null) {
            subTypes = new ArrayList<>();
        }

        subTypes.add(subNodeType);
    }

    /**
     * Sets the sub types to the node. These types will not affect the structural type of the node.
     * @param subNodeTypes The node types to add to the node.
     */
    public void setSubNodeTypes(List<NodeType> subNodeTypes) {
        this.subTypes = subNodeTypes;
    }

    /**
     * Gets the sub types of the node. These will not include the primary type for the node that affects the tree structure.
     * @return A list of node types that represent the sub types on the node, or null if no sub types have been set.
     */
    public List<NodeType> getSubNodeTypes() {
        return subTypes;
    }

    /**
     * @return Ignored status.
     */
    public boolean isIgnored() {
        return ignored;
    }

    /**
     * Set ignored status.
     * 
     * @param status The new ignored status of the node.
     */
    public void setIgnored(boolean status) {
        this.ignored = status;
    }

    /**
     * @return Whether or not the node is a leaf.
     */
    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
    
    /**
     * @return Whether or not the node has children.
     */
    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    /**
     * @return Whether or not node is the root of the tree.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Do a pre-order tree traversal.
     * 
     * @param consumer
     */
    public void walk(Consumer<Node> consumer) {
        consumer.accept(this);
        
        if (children != null) {
            children.forEach(child -> child.walk(consumer));
        }
    }
    
    /**
     * Remove all node types set on node.
     */
    public void clearNodeTypes() {
        setNodeType(null);
        setSubNodeTypes(null);
    }
    
    /**
     * Nodes are considered equal if their identifiers are equal.
     */
    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Node)) {
            return false;
        }

        Node node = (Node) o;

        if (identifier != null ? !identifier.equals(node.identifier) :
            node.identifier != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Node [identifier=" + identifier + ", parent=" + (parent == null ? "null" : parent.getIdentifier()) + ", domainObject=" + domainObject + ", fileInfo=" + fileInfo + "]";
    }
}
