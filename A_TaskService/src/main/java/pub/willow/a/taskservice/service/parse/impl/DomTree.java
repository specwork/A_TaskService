package pub.willow.a.taskservice.service.parse.impl;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.html.dom.HTMLElementImpl;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import pub.willow.a.taskservice.utils.RegexUtil;

public class DomTree {
	
	// 相似子树的相似度阈值
	public static final float SIMILARITY = 0.4f;
	
	// 搜索引擎的关键词搜索时,命中的关键词会用以下字符高亮
	public static final String[] SE_KEYWORD_TAG = new String[]{"EM"};
	
	/**
     * 获取文档对象
     * @param html	html源码
     * @return	文档对象
     * @throws SAXException
     * @throws IOException
     */
    public static Document getDocument(String html) throws SAXException, IOException {
    	if(html == null)
    		return null;
    	
    	DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new StringReader(html)));
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        Document document = parser.getDocument(); 
        // 如果根节点是#document，则把该节点的子节点作为根节点
        /*if(node.getNodeName().equals("#document")) {
        	node = node.getFirstChild();
        }*/
        return document;
    }
    
    
    /**
     * 用于过滤经过过滤之后的html乱码,如有大于号">"误认为>,则无法处理剩余html部分
     * @param html
     * @return
     */
    public static String fiterHtml(String html) {
		if(html == null || html.length() == 0 || html.indexOf(">") == -1)
			return html;
		
		String fiterHtml = html;
		try {
			Node node = DomTree.getDocument(html);
			if(node != null) {
				NodeList nodeList = node.getChildNodes();
				for(int i=0; i<nodeList.getLength(); i++) {
					Node childNode = nodeList.item(i);
					if(childNode instanceof HTMLElementImpl && childNode.getNodeName().equals("HTML")) {
						fiterHtml = childNode.getTextContent();
					}
				}
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fiterHtml;
	}
    
    
    /**
     * 寻找forum源码的起始节点
     * @param node	forum源码的节点
     * @param rootNodeName	根节点的名字
     * @param childNodeName	根节点的孩子节点的名字
     * @return
     */
    public static Node findStartNode(Node node, String rootNodeName, String childNodeName) {
    	Node startNode = null;
    	
    	NodeList nodeList = node.getChildNodes();
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if(childNode instanceof HTMLElementImpl) {
				if(childNode.getNodeName().equals(rootNodeName) || childNodeName.indexOf(childNode.getNodeName()) != -1) {
					Node parentNode = childNode.getParentNode();
					return parentNode;
				} else {
					childNode = findStartNode(childNode, rootNodeName, childNodeName);
					if(childNode != null) 
						return childNode;
				}
			}
    	}
    	
    	return startNode;
	}
    
    /**
     * 获取HTML的前序遍历字符串
     * @param node	根节点
     * @return	返回html源码的前序遍历字符串
     * @throws IOException 
     * @throws SAXException 
     */
    public static String getHtmlTreePreTraverseStr(String html) throws SAXException, IOException {
    	// Node startNode = getStartNode(html);
    	Node node = getDocument(html);
    	
    	if(node != null) {
    		String headNodeName = RegexUtil.getMatchInfoSingle(html, "<(.*?)[\\s>]").toUpperCase();
    		String preTraverseStr = getTreePreTraverseStr(node);
    		preTraverseStr = preTraverseStr.substring(preTraverseStr.indexOf(headNodeName));
    		return preTraverseStr;
    	}
    	
    	return null;
    }
    
    /**
     * 获取节点的开始节点，即一段源码的开始节点，因为如源码如下："<tr><td></td></tr>"，dom树解析之后，树的起始节点依然是html，所以要找该段
     * 源码对应的起始节点，即tr节点
     * @param html	html源码
     * @return	html开始节点
     * @throws SAXException
     * @throws IOException
     */
    public static Node getStartNode(String html) throws SAXException, IOException {
    	Node rootNode = getDocument(html); 
    	String headNodeName = RegexUtil.getMatchInfoSingle(html, "<(.*?)[\\s>]").toUpperCase();
    	
    	Node startNode = findStartNode(rootNode, headNodeName);
    	return startNode;
    }
    
    /**
     * 获取节点的开始节点，即一段源码的开始节点，因为如源码如下："<tr><td></td></tr>"，dom树解析之后，树的起始节点依然是html，所以要找该段
     * 源码对应的起始节点，即tr节点
     * @param node	根节点
     * @param headNodeName	源码的起始节点的名字
     * @return	若找到起始节点，则返回起始节点，否则，返回null
     */
    public static Node findStartNode(Node node, String headNodeName) {
    	Node startNode = null;
    	
    	if(node.getNodeName().equals(headNodeName))
    		return node;
    	
    	NodeList nodeList = node.getChildNodes();
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if(childNode instanceof HTMLElementImpl) {
				startNode = findStartNode(childNode, headNodeName);
				if(startNode != null)
					return startNode.getParentNode();
			}
    	}
    	
    	return startNode;
    }
    
    /**
     * 获取以node为根节点的树的前序遍历字符串
     * @param node	节点
     * @return	以node为根节点的树的前序遍历字符串
     */
    public static String getTreePreTraverseStr(Node node) {
    	StringBuffer preTraverseStr = new StringBuffer();
    	
    	if(node == null)
    		return "";
    	
    	if(node instanceof HTMLElementImpl) {
    		boolean isSEKeywordTagFlag = false;
    		for(String seKeywordTag : SE_KEYWORD_TAG) {
    			if(seKeywordTag.equals(node.getNodeName())) {
    				isSEKeywordTagFlag = true;
    			}
    		}
    		
    		if(!isSEKeywordTagFlag)
    			preTraverseStr.append(node.getNodeName() + ",");
    	}
    	
    	
    	NodeList nodeList = node.getChildNodes();
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if(childNode instanceof HTMLElementImpl) {
				preTraverseStr.append(getTreePreTraverseStr(childNode));
			}
		}	
    	
    	return preTraverseStr.toString();
    }
    
    public static int getNodeTagNum(Node node, String nodeContent) {
    	int nodeTagNum = 0;
    	
    	String preTraverseStr = getTreePreTraverseStr(node);
    	if(preTraverseStr != null && !preTraverseStr.equals("")) {
    		int aTagNum = 0;
    		String[] tags = preTraverseStr.split(",");
    		for(String tag : tags) {
    			if(!tag.equals("DIV") && !tag.equals("FONT") && !tag.equals("SPAN") && !tag.equals("B") && !tag.equals("TR") && !tag.equals("TD") && !tag.equals("P") && !tag.equals("BR") && !tag.equals("STRONG") && !tag.equals("IMG") && !tag.equals("OBJECT") && !tag.equals("PARAM") && !tag.equals("O:P") && !tag.equals("LINK") && !tag.equals("OL")) {
//System.out.println(tag);
    				nodeTagNum++;
    			}
    			if(tag.equals("A"))
    				aTagNum++;
    		}

    		/**
			 * 防止抽取到地区这种信息,如:华北北京|天津|石家庄|唐山|秦皇岛|邯郸|邢台|保定|张家口|承德沧州|廊坊|衡水|济南|青岛|淄博|潍坊|泰安|威海|临沂德州|聊城|滨州|太原|大同|阳泉|长治|朔州|晋中|临汾呼和浩特华南广州|深圳|珠海|佛山|湛江|惠州|阳江|东莞|中山|南宁柳州|福州|厦门|漳州|南昌|九江|上饶|海口华东上海|杭州|宁波|温州|嘉兴|绍兴|衢州|舟山|南京|无锡徐州|苏州|南通|淮安|盐城|扬州|镇江|泰州|宿迁|合肥芜湖|蚌埠|安庆华中武汉|襄阳|恩施|长沙|衡阳|邵阳|郑州|洛阳|平顶山|新乡焦作|南阳|信阳东北沈阳|大连|鞍山|长春|哈尔滨|齐齐哈尔|鸡西|伊春|佳木斯|绥化西南重庆|成都|德阳|绵阳|乐山|南充|昆明|曲靖|玉溪|保山楚雄|红河|文山|西双版纳|大理|贵阳|六盘水|遵义|安顺|铜仁毕节|都匀|凯里|兴义西北西安|咸阳|渭南|延安|榆林|安康
			 */
    		if((float)aTagNum/tags.length < 0.45 || nodeContent.length() > 500) {
    			nodeTagNum = nodeTagNum - aTagNum;
    		}
    	}
    	
    	if(nodeTagNum == 0)
    		nodeTagNum = 1;
    	
    	return nodeTagNum;
    }
    
    public static void main(String[] args) {
    	String s = "华北北京|天津|石家庄|唐山|秦皇岛|邯郸|邢台|保定|张家口|承德沧州|廊坊|衡水|济南|青岛|淄博|潍坊|泰安|威海|临沂德州|聊城|滨州|太原|大同|阳泉|长治|朔州|晋中|临汾呼和浩特华南广州|深圳|珠海|佛山|湛江|惠州|阳江|东莞|中山|南宁柳州|福州|厦门|漳州|南昌|九江|上饶|海口华东上海|杭州|宁波|温州|嘉兴|绍兴|衢州|舟山|南京|无锡徐州|苏州|南通|淮安|盐城|扬州|镇江|泰州|宿迁|合肥芜湖|蚌埠|安庆华中武汉|襄阳|恩施|长沙|衡阳|邵阳|郑州|洛阳|平顶山|新乡焦作|南阳|信阳东北沈阳|大连|鞍山|长春|哈尔滨|齐齐哈尔|鸡西|伊春|佳木斯|绥化西南重庆|成都|德阳|绵阳|乐山|南充|昆明|曲靖|玉溪|保山楚雄|红河|文山|西双版纳|大理|贵阳|六盘水|遵义|安顺|铜仁毕节|都匀|凯里|兴义西北西安|咸阳|渭南|延安|榆林|安康";
    	System.out.println(s.length());
    }
    
    /**
     * 获取根节点
     * @param htmlNode	整个网页的节点
     * @param forumPreTraverseStr	一个帖子节点树的前序遍历字符串
     * @param forumContent	一个帖子的内容
     * @return	若找到根节点，返回根节点的名字，否则，返回null
     */
    public static Node getRootNode(Node htmlNode, String forumPreTraverseStr, String forumContent) {
    	Node rootNode = null;
    	for(float similarity=1.0f; similarity>=0.4; similarity=(float)(similarity-0.1)) {
    		rootNode = findRootNode(htmlNode, forumPreTraverseStr, forumContent, similarity);
    		if(rootNode != null) {
    			return rootNode;
    		} 
    	}
    	
    	return rootNode;
    }

    /**
     * 寻找根节点
     * @param htmlNode	当前根节点
     * @param forumPreTraverseStr	一个帖子节点树的前序遍历字符串
     * @param forumContent	一个帖子的内容
     * @param similarityThreshold	树匹配的相似度阈值
     * @return	若找到根节点，返回根节点，否则，返回null
     */
    private static Node findRootNode(Node htmlNode, String forumPreTraverseStr, String forumContent, float similarityThreshold) {
    	Node rootNode = null;
    	// 树节点和帖子树计算出的相似度
    	float similarity = 0f;
    	// 树节点的前序遍历字符串
    	String nodePreTraverseStr = "";
    	
    	NodeList nodeList = htmlNode.getChildNodes();
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if(childNode instanceof HTMLElementImpl) {
				// 计算该节点的前序遍历字符串
				nodePreTraverseStr = getTreePreTraverseStr(childNode);
				// 计算该树节点和帖子节点的相似度
				similarity = EditDistance.similarity(nodePreTraverseStr, forumPreTraverseStr);
				
				// 如果相似度大于相似度阈值
				if(similarity >= similarityThreshold && contains(childNode, forumContent)) {
					return childNode;
				} else {
					// 递归遍历该节点的子节点
					rootNode = findRootNode(childNode, forumPreTraverseStr, forumContent, similarityThreshold);
					if(rootNode != null) 
						return rootNode;
				}
			}
		}
    	
		return rootNode;
	}
    
    
    
    private static boolean contains(Node node, String text) {
    	boolean contains = true;
    	
    	if(node.getChildNodes().getLength() == 1) {
    		if(node.getTextContent().trim().length() >= 2 && text.indexOf(node.getTextContent().trim()) == -1) {
    			contains = false;
    		}
    	} else {
    		NodeList nodeList = node.getChildNodes();
        	for(int i=0; i<nodeList.getLength(); i++) {
    			Node childNode = nodeList.item(i);
    			if(childNode instanceof HTMLElementImpl) {
    				if(contains(childNode, text))
    					break;
    			}
        	}
    	}
    	
    	return contains;
    }
    
    /**
     * 获取相似子树节点
     * @param html	网页源码
     * @param rootNodeName	根节点的名字	
     * @param standardPreTraverseStr	标准树的前序遍历字符串
     * @param childNodeName	根节点的子节点的名字
     * @return	如果找到相似子树，返回相似子树集合，否则，返回null
     * @throws IOException 
     * @throws SAXException 
     */
    public static List<Node> getSimilarityTree(String html, String rootNodeName, String standardPreTraverseStr, String childNodeName, Node standardTree) throws SAXException, IOException {
    	Document document = getDocument(html);
    	List<Node> nodeList = new ArrayList<Node>();
    	nodeList = getSimilarityTree(document, rootNodeName, standardPreTraverseStr, childNodeName, nodeList, document, standardTree);
    	
    	// 该节点作为根节点，把根节点的第一个节点作为根节点
    	NodeList childNodeList = document.getChildNodes();
    	if(childNodeList.getLength() > 0)
    		nodeList.add(childNodeList.item(0)); 
    	
    	return nodeList;
    }
    
    /**
     * 获取相似子树节点
     * @param node	网页源码的节点
     * @param rootNodeName	根节点的名字	
     * @param standardPreTraverseStr	标准树的前序遍历字符串
     * @param childNodeName	根节点的子节点的名字
     * @param similarityNodeList	相似子树的节点集合
     * @return	如果找到相似子树，返回相似子树集合，否则，返回null
     */
	private static List<Node> getSimilarityTree(Node node, String rootNodeName, String standardPreTraverseStr, String childNodeName, List<Node> similarityNodeList, Document document, Node standardTree) {
		NodeList nodeList = node.getChildNodes();
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if(childNode instanceof HTMLElementImpl) {
				if(isSimilarityTree(childNode, rootNodeName, standardPreTraverseStr, childNodeName)) {						// 如果满足条件
					List<Node> nl = new ArrayList<Node>();
					nl.add(childNode);
//System.out.println("++++content : " + childNode.getTextContent());
//System.out.println("++++++++pre : " + DomTree.getTreePreTraverseStr(childNode));
// #######新的信息 start#####					
//nl = DomTree.treeAlignment(standardTree, nl, document, true);
//System.out.println("++++++after : " + DomTree.getTreePreTraverseStr(nl.get(0)));
// #######新的信息 end###########
					similarityNodeList.add(nl.get(0));
				} else {
					getSimilarityTree(childNode, rootNodeName, standardPreTraverseStr, childNodeName, similarityNodeList, document, standardTree);
				}
			}
		}	
		
		return similarityNodeList;
	}
	
	private static boolean isSimilarityTree(Node node, String rootNodeName, String standardPreTraverseStr, String childNodeName) {
		boolean isSimilarityTree = false;
		
		/**
		 * 1、如果两棵树的根节点相同
		 * 2、目标子树的根节点的子节点都包含在根节点的子节内
		 * 3、两棵树的相似度大于等于similarity
		 * 
		 */
		// 1、如果两棵树的根节点相同
		if(node.getNodeName().equals(rootNodeName)) {
			
			// 2、目标子树的根节点的子节点都包含在根节点的子节内
			NodeList nodeList = node.getChildNodes();
	    	for(int i=0; i<nodeList.getLength(); i++) {
				Node childNode = nodeList.item(i);
				if(childNode instanceof HTMLElementImpl) {
					if(!childNodeName.contains(childNode.getNodeName()+",")) {
						return false;
					}
				}
	    	}
	    	
	    	// 3、两棵树的相似度大于等于similarity  
	    	String preTraverseStr = getTreePreTraverseStr(node);
	    	if(EditDistance.similarity(standardPreTraverseStr, preTraverseStr) >= SIMILARITY) {
	    		return true;
	    	}
		}
		
		return isSimilarityTree;
	}
	
	/**
     * 获取以node为根节点的树的前序遍历字符串
     * @param node	节点
     * @return	以node为根节点的树的前序遍历字符串
     */
    /*public static Node getTreeAlignmentNode(Node node, String[] nodeNameArray, int index) {
    	if(node == null)
    		return node;
    	
    	NodeList nodeList = node.getChildNodes();
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			if(childNode instanceof HTMLElementImpl) {
				
				index++;
				childNode = getTreeAlignmentNode(childNode, nodeNameArray, index);
			}
		}	
    	
    	return node;
    }
    
    public static Node operateNode(Node standardNode, Node compareNode) {
    	if(standardNode != null && compareNode != null) {
    		if(standardNode.getNodeName().equals(compareNode.getNodeName()) && standardNode.getParentNode().getNodeName().equals(compareNode.getParentNode().getNodeName())) {
    			// 如果该节点的名字相同并且父节点的名字相同,则认为这两个节点是相同的节点
    			
    		} else {
    			
    		}
    	}
    	
    	return compareNode;
    }
	
	*//**
	 * 新树对齐算法
	 * @param standardTree	标准树
	 * @param alignmentTreeList	需要对齐的树的列表	
	 * @param document	整个文档对象
	 * @param isFirstNode	是否是第一个节点
	 *//*
	public static List<Node> treeAlignment(String standardTreePreStr, List<Node> alignmentTreeList, Document document, boolean isFirstNode) {
		if(alignmentTreeList == null || alignmentTreeList.size() == 0)
			return null;
		
		String[] nodeNameArray = standardTreePreStr.split(",");
		int index = 0;
		for(Node alignmentNode : alignmentTreeList) {
			index = 0;
			alignmentNode = getTreeAlignmentNode(alignmentNode, nodeNameArray, index);
		}
		
    	return alignmentTreeList;
	}*/
	
	
	/**
	 * 树对齐算法
	 * @param standardTree	标准树
	 * @param alignmentTreeList	需要对齐的树的列表	
	 * @param document	整个文档对象
	 * @param isFirstNode	是否是第一个节点
	 */
	public static List<Node> treeAlignment(Node standardTree, List<Node> alignmentTreeList, Document document, boolean isFirstNode) {
		NodeList nodeList = standardTree.getChildNodes();
		
		// 标准树节点的索引
		int standardIndex = 0;
		
    	for(int i=0; i<nodeList.getLength(); i++) {
			Node childNode = nodeList.item(i);
			Node cloneChildNode = childNode.cloneNode(false);
			if(cloneChildNode instanceof HTMLElementImpl) {
				// 遍历需要对齐的树列表
				List<Node> childAlignmentTreeList = new ArrayList<Node>();
				for(Node alignmentNode : alignmentTreeList) {
					if(alignmentNode != null) {
						Node tempAlignmentNode = getChildNode(alignmentNode, standardIndex);
						if(!(tempAlignmentNode instanceof HTMLElementImpl)) {
							cloneChildNode = document.importNode(cloneChildNode, true);
							alignmentNode.appendChild(cloneChildNode);
							tempAlignmentNode = alignmentNode;
							continue;
						}
						
						if(tempAlignmentNode != null) {
							cloneChildNode.setTextContent(tempAlignmentNode.getTextContent());
						}
		
						// 找到标准树的第一个和相似树相同的节点
						if(isFirstNode && tempAlignmentNode != null) {
							if(!cloneChildNode.getNodeName().equals(tempAlignmentNode.getNodeName())) {
								alignmentTreeList = treeAlignment(childNode, alignmentTreeList, document, isFirstNode);
								return alignmentTreeList;
							}
						}
						
						if(tempAlignmentNode == null) {			// 如果对齐的节点为空，直接把节点添加到对齐树的后面
							cloneChildNode = document.importNode(cloneChildNode, true);
							alignmentNode.appendChild(cloneChildNode);
							tempAlignmentNode = alignmentNode;
						} else if(!isEquals(cloneChildNode, tempAlignmentNode)) {				// 如果两个节点不相同
							int flag = isSimilarNode(tempAlignmentNode, cloneChildNode, alignmentNode, standardTree);	// 计算该节点到底是应该删除，还是添加，还是保持不变
							if(flag == 1) {							// 1表示添加新的节点
								cloneChildNode = document.importNode(cloneChildNode, true);
								alignmentNode.insertBefore(cloneChildNode, tempAlignmentNode);
							} else if(flag == 2) {					// 2表示删除节点，如果该节点在标准树节点的兄弟节点中不能找到，则删除该节点
								Node firstChildNode = getChildNode(tempAlignmentNode, 0);
								
								if(firstChildNode != null) {	// 如果有子节点，则用子节点替换该节点
									firstChildNode = document.importNode(firstChildNode, true);		
									alignmentNode.replaceChild(firstChildNode, tempAlignmentNode);
								} else {
									alignmentNode.removeChild(tempAlignmentNode);
								}
								i--;
								standardIndex--;
							} else if(flag == 0) {					// 0表示两个节点相同
								isFirstNode = false;
								childAlignmentTreeList.add(tempAlignmentNode);
							}
						} else {
							isFirstNode = false;
							childAlignmentTreeList.add(tempAlignmentNode);
						}
					}
					
					
				}
				standardIndex++;
				
				treeAlignment(childNode, childAlignmentTreeList, document, isFirstNode);
			}
    	}
    	
    	return alignmentTreeList;
	}
	
	/**
	 * 获取孩子节点，
	 * @param parentNode
	 * @param index
	 * @return
	 */
	public static Node getChildNode(Node parentNode, int index) {
		Node node = null;
		
		if(parentNode == null)
			return node;
		
		NodeList nodeList = parentNode.getChildNodes();
		if(index < nodeList.getLength()) {	
			int alignmentIndex = 0;
			for(int k=0; k<nodeList.getLength(); k++) {
				node = nodeList.item(k);
				if(node instanceof HTMLElementImpl) {
					if(alignmentIndex == index) {
						break;
					}
					alignmentIndex++;
				}
			}
		}
		
		return node;
	}
	
	/**
	 * alignmentNode和standardNode是否相似
	 * @param alignmentNode	
	 * @param standardNode
	 * @param alignmentParentNode
	 * @param standardParentNode
	 * @return
	 */
	public static int isSimilarNode(Node alignmentNode, Node standardNode, Node alignmentParentNode, Node standardParentNode) {
		int flag = 0;	// 0表示两个节点相同，1表示应该在该节点前面添加一个节点，2表示删除该节点
		
		// 如果两个节点不相同，则删除该节点
		if(!alignmentNode.getNodeName().equals(standardNode.getNodeName())) 
			return 2;
		
		// 计算标准树中相同节点
		List<Node> standardSameNodeList = findNode(standardParentNode, standardNode.getNodeName());
		
		// 计算对齐树中相同节点
		List<Node> alignmentSameNodeList = findNode(alignmentParentNode, alignmentNode.getNodeName());
		
		// 如果对齐树的该节点比标准树的该节点多，那么判断是不是该节点需要删除
		if(alignmentSameNodeList.size() > standardSameNodeList.size()) {
			float alignmentNodeScore = gradeNodeNew(alignmentNode, standardNode);
			
			float score = 0.0f;
			for(Node alignmentSameNode : alignmentSameNodeList) {
				score = gradeNodeNew(alignmentSameNode, standardNode);
				if(score > alignmentNodeScore) {
					flag = 2;
					break;
				}
			}
		} else if(alignmentSameNodeList.size() < standardSameNodeList.size()) {		// 如果对齐树的该节点比标准树的该节点少，那么判断是不是需要添加一个新的节点
			float alignmentNodeScore = gradeNodeNew(alignmentNode, standardNode);
			
			float score = 0.0f;
			for(Node standardSameNode : standardSameNodeList) {
				score = gradeNodeNew(standardSameNode, alignmentNode);
				if(score > alignmentNodeScore) {
					flag = 1;
					break;
				}
			}
		}
		
if(flag == 2) {
	System.out.println("alignment node name : " + alignmentNode.getNodeName() + "  size : " + alignmentSameNodeList.size() + "  standard node name : " + standardNode.getNodeName() + "  size : " + standardSameNodeList.size());
}
		
		return flag;
	}
	
	/**
	 * 寻找node节点子节点的名字和nodeName相同的节点
	 * @param node	需要寻找	
	 * @param nodeName	
	 * @return
	 */
	public static List<Node> findNode(Node node, String nodeName) {
		List<Node> nodeList = new ArrayList<Node>();
		
		if(node == null)
			return nodeList;
		
		NodeList childNodeList = node.getChildNodes();
		if(childNodeList != null) {
			for(int i=0; i<childNodeList.getLength(); i++) {
				Node childNode = childNodeList.item(i);
				if(childNode instanceof HTMLElementImpl) {
					if(childNode.getNodeName().equals(nodeName)) {
						nodeList.add(childNode);
					}
				}
			}
		}
		
		return nodeList;
	}
	

	/**
	 * 节点打分，依据标准节点给节点打分
	 * @param gradeNode	打分的节点
	 * @param standardNode	标准的节点
	 * @return	节点分数
	 */
	public static float gradeNode(Node gradeNode, Node standardNode) {
		float score = 0.0f;
		
		if(gradeNode == null || standardNode == null)
			return score;

		NamedNodeMap gradeNamedNodeMap = gradeNode.getAttributes();
		NamedNodeMap standardNamedNodeMap = gradeNode.getAttributes();
		if(standardNamedNodeMap.getLength() == 0)
			return score;
		
		float attributeScore = 1.0f/standardNamedNodeMap.getLength();
		
		for(int i=0; i<standardNamedNodeMap.getLength(); i++) {
			if(i >= gradeNamedNodeMap.getLength())
				break;
			
			Node standardNamedNode = standardNamedNodeMap.item(i);
			score = score + gradeAttribute(gradeNamedNodeMap, standardNamedNode, attributeScore);
		}
		
		return score;
	}

	// 新的节点打分机制start
	/**
	 * 新的节点大分机制
	 * @param gradeNode
	 * @param standardNode
	 * @return
	 */
	public static float gradeNodeNew(Node gradeNode, Node standardNode) {
		float score = 0.0f;
		
		String gradeAttributeNameStr = getAttributeNameStr(gradeNode);
		String standardAttributeNameStr = getAttributeNameStr(standardNode);
		if(gradeAttributeNameStr != null && standardAttributeNameStr != null)
			score = EditDistance.similarity(standardAttributeNameStr, gradeAttributeNameStr);
		
		return score;
	}
	
	/**
	 * 获取节点属性名字字符串
	 * @param gradeNode
	 * @return
	 */
	public static String getAttributeNameStr(Node gradeNode) {
		String attributeNameStr = "";
		if(gradeNode == null)
			return attributeNameStr;
		
		NamedNodeMap NamedNodeMap = gradeNode.getAttributes();
		if(NamedNodeMap != null) {
			for(int i=0; i<NamedNodeMap.getLength(); i++) {
				Node gradeNamedNode = NamedNodeMap.item(i);
				attributeNameStr = attributeNameStr + gradeNamedNode.getNodeName() + ",";
			}
		}
		
		return attributeNameStr;
	}
	// 新的节点打分机制end
	
	/**
	 * 给节点属性打分
	 * @param gradeNamedNodeMap	需要打分的节点属性	
	 * @param standardNamedNode	需要对比的节点属性
	 * @param attributeScore	该节点属性满分
	 * @return
	 */
	public static float gradeAttribute(NamedNodeMap gradeNamedNodeMap, Node standardNamedNode, float attributeScore) {
		float score = 0.0f;
		
		for(int i=0; i<gradeNamedNodeMap.getLength(); i++) {
			Node gradeNamedNode = gradeNamedNodeMap.item(i);
			if(gradeNamedNode.getNodeName().equals(standardNamedNode.getNodeName())) {
				score = score + attributeScore/2;
				if(gradeNamedNode.getTextContent().trim().equals(standardNamedNode.getTextContent().trim())) {
					score = score + attributeScore/2;
				}
				break;
			}
		}
		
		return score;
	}
	
	
	public static boolean isEquals(Node nodeA, Node nodeB) {
		if(nodeA == null || nodeB == null)
			return false;
		
		boolean isEquals = false;
		
		if(nodeA.getNodeName().equals(nodeB.getNodeName())) {	// 如果两个节点名字相同
			NamedNodeMap namedNodeMapA = nodeA.getAttributes();
			NamedNodeMap namedNodeMapB = nodeB.getAttributes();
			
			if(namedNodeMapA.getLength() == namedNodeMapB.getLength()) {
				int i = 0;
				for(i=0; i<namedNodeMapA.getLength(); i++) {
					Node tempNodeA = namedNodeMapA.item(i);
					Node tempNodeB = namedNodeMapB.item(i);
					
					if(tempNodeA.getNodeName().equals("href"))	// 如果是href，则跳过，因为链接基本都不一样 
						continue;

					if(tempNodeA.getNodeName().indexOf("color") != -1) 	// 如果是color，则跳过，因为color的属性有可能不同
						continue;
					
					if(tempNodeA.getNodeName().equals("id"))				// 如果是id属性，基本都不一样
						continue;
					
//System.out.println("nodeA : " + tempNodeA.getNodeName() + "   nodeB : " + tempNodeB.getNodeName());		
//System.out.println("A content : " + tempNodeA.getTextContent().trim());
//System.out.println("B content : " + tempNodeB.getTextContent().trim());
					if(!(tempNodeA.getNodeName().equals(tempNodeB.getNodeName()) && tempNodeA.getTextContent().trim().equals(tempNodeB.getTextContent().trim()))) {
						break;
					} 
				}
				if(i == namedNodeMapA.getLength())
					isEquals = true;
			} else {
//				System.out.println("length not equals : nodeA : " + nodeA.getNodeName() + "   nodeB : " + nodeA.getNodeName());
//				System.out.println("--A content : " + nodeA.getTextContent().trim());
//				System.out.println("--B content : " + nodeB.getTextContent().trim());
			}
			
//System.out.println(nodeA.getNodeName() + " and " + nodeB.getNodeName() + " is equals : " + isEquals);	
//System.out.println("==================================================================");
			
		}
		
		return isEquals;
	}
	
	/**
	 * 是否在标准树的兄弟节点中
	 * @param standardNode	标准树节点
	 * @param node	对齐树节点
	 * @return	如果在标准树的兄弟节点中，返回TRUE，否则，返回FALSE
	 */
	public static boolean isInBrotherNode(Node standardNode, Node node) {
		boolean isInBrotherNode = false; 
		
		NodeList nodeList = standardNode.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++) {
			Node standChildNode = nodeList.item(i);
			if(isEquals(standChildNode, node)) {
				isInBrotherNode = true;
			}
		}
		
		return isInBrotherNode;
	}
    
}
