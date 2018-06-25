## KotlinLinkedBreakLineView

### Adding dependency in app/build.gradle

```
    compile project(":linkedbreaklineview")
```

### Using View in Activity

```
    val view = LinkedBreakLineView.create(this)
```

### Adding Listener in LinkedBreakLineView

```
    view.addLinkedBreakLineListener {
        Toast.makeText(this, "${it+1} animation completed", Toast.LENGTH_SHORT).show()
    }
```

### Demo

<img src="https://github.com/Anwesh43/KotlinLinkedBreakLineView/blob/master/screenshots/linkedbreaklineview.gif" width="350px" height="600px">
