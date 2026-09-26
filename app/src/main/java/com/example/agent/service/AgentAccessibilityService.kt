package com.example.agent.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SemanticUiNode(
    val className: String,
    val text: String?,
    val contentDescription: String?,
    val viewIdResourceName: String?,
    val isClickable: Boolean,
    val isEditable: Boolean,
    val isScrollable: Boolean,
    val bounds: Rect
)

class AgentAccessibilityService : AccessibilityService() {
    companion object {
        var instance: AgentAccessibilityService? = null
            private set

        private val _isServiceEnabled = MutableStateFlow(false)
        val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

        private val _lastCapturedPackage = MutableStateFlow<String>("")
        val lastCapturedPackage: StateFlow<String> = _lastCapturedPackage.asStateFlow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceEnabled.value = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.packageName?.let {
            _lastCapturedPackage.value = it.toString()
        }
    }

    override fun onInterrupt() {
        instance = null
        _isServiceEnabled.value = false
    }

    override fun onDestroy() {
        instance = null
        _isServiceEnabled.value = false
        super.onDestroy()
    }

    fun findNodesByText(query: String): List<AccessibilityNodeInfo> {
        val root = rootInActiveWindow ?: return emptyList()
        return root.findAccessibilityNodeInfosByText(query) ?: emptyList()
    }

    fun findNodesByViewId(viewId: String): List<AccessibilityNodeInfo> {
        val root = rootInActiveWindow ?: return emptyList()
        return root.findAccessibilityNodeInfosByViewId(viewId) ?: emptyList()
    }

    fun performClickOnNode(node: AccessibilityNodeInfo): Boolean {
        var target: AccessibilityNodeInfo? = node
        while (target != null) {
            if (target.isClickable) {
                return target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            target = target.parent
        }
        return false
    }

    fun fillFormField(nodeInfo: AccessibilityNodeInfo, text: String): Boolean {
        return if (nodeInfo.isEditable) {
            val arguments = Bundle()
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
        } else {
            false
        }
    }

    fun performScrollForward(nodeInfo: AccessibilityNodeInfo): Boolean {
        return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }

    fun clickCoordinates(x: Float, y: Float, onComplete: () -> Unit = {}) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, 50)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                onComplete()
            }
        }, null)
    }

    fun extractSemanticUiTree(): List<SemanticUiNode> {
        val root = rootInActiveWindow ?: return emptyList()
        val result = mutableListOf<SemanticUiNode>()
        traverseNodeTree(root, result)
        return result
    }

    private fun traverseNodeTree(node: AccessibilityNodeInfo, list: MutableList<SemanticUiNode>) {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        list.add(
            SemanticUiNode(
                className = node.className?.toString() ?: "",
                text = node.text?.toString(),
                contentDescription = node.contentDescription?.toString(),
                viewIdResourceName = node.viewIdResourceName,
                isClickable = node.isClickable,
                isEditable = node.isEditable,
                isScrollable = node.isScrollable,
                bounds = bounds
            )
        )
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            traverseNodeTree(child, list)
        }
    }
}
