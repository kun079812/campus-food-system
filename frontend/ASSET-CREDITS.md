# 视觉资源

- 校园背景：使用 imagegen 技能和内置 image_gen 工具生成，未使用 CLI/API 回退。按参考图的水彩校园、浅蓝天空、粉色樱花和奶油色建筑方向制作。
- 项目内路径：`E:/university/project_term_four/homework/campus-food-order-web/public/images/campus-spring.png`。
- 登录页和首页共用这张背景，由 CSS 控制位置和渐变遮罩。
- 食物与功能图标：项目中可编辑的 SVG 组件，分别在 `FoodArt.vue` 和 `UiIcon.vue`。它们用于无菜品照片时的插画与页面功能图标。
- 字体：使用操作系统提供的中文字体，不依赖外部字体服务。

背景图最终生成提示词：

> Use case: illustration-story. Asset type: production website hero and login background for a Chinese campus canteen ordering app. Create a beautiful panoramic 16:9 pastel watercolor illustration of a peaceful Chinese university campus in spring: small arched bridge over a reflective lake, pale cream university buildings and a distant clocktower, pink cherry blossoms, soft sage green and yellow trees, delicate lavender wildflowers framing the bottom edge, blue sky and fluffy clouds. Detailed but airy Japanese storybook watercolor gouache, fine paper texture, gentle sunlight, cheerful soothing mood. Palette icy sky blue, buttercream, blush pink, lavender, light leafy green. Composition: landscape scene across the full image, the distant campus and bridge at center-right; upper left has generous light blue sky space for website copy placed later in code. No people. No text, no letters, no logos, no buttons, no user interface, no panels, no borders, no watermark. This is a scenic background asset, NOT a website screenshot.
