<template>
  <div>
    <canvas ref="canvas" width="800" height="400"></canvas>
  </div>
</template>

<script>
export default {
  mounted() {
    this.startAnimation();
  },
  methods: {
    startAnimation() {
      const canvas = this.$refs.canvas;
      const context = canvas.getContext("2d");

      const numRows = 4;
      const numCols = 8;
      const rectWidth = canvas.width / numCols;
      const rectHeight = canvas.height / numRows;

      let frameCount = 0;
      let row = 0;
      let col = 0;

      const drawFrame = () => {
        // 清空Canvas
        context.clearRect(0, 0, canvas.width, canvas.height);

        // 计算矩形位置
        const x = col * rectWidth;
        const y = row * rectHeight;
        const rectColor = this.getRandomColor();

        // 绘制矩形
        context.fillStyle = rectColor;
        context.fillRect(x, y, rectWidth, rectHeight);

        if (col === 0) {
          const arrowX1 = x + rectWidth / 2;
          const arrowY1 = y + rectHeight / 2;
          const arrowX2 = (numCols - 1) * rectWidth + rectWidth / 2;
          const arrowY2 = row * rectHeight + rectHeight / 2;
          const arrowColor = this.getRandomColor();

          // 绘制箭头
          context.beginPath();
          context.moveTo(arrowX1, arrowY1);
          context.lineTo(arrowX2, arrowY2);
          context.strokeStyle = arrowColor;
          context.lineWidth = 2;
          context.stroke();

          // 添加箭头头部
          const arrowHeadSize = 10;
          const angle = Math.atan2(arrowY2 - arrowY1, arrowX2 - arrowX1);
          context.fillStyle = arrowColor;
          context.beginPath();
          context.moveTo(
              arrowX2 - arrowHeadSize * Math.cos(angle - Math.PI / 6),
              arrowY2 - arrowHeadSize * Math.sin(angle - Math.PI / 6)
          );
          context.lineTo(arrowX2, arrowY2);
          context.lineTo(
              arrowX2 - arrowHeadSize * Math.cos(angle + Math.PI / 6),
              arrowY2 - arrowHeadSize * Math.sin(angle + Math.PI / 6)
          );
          context.closePath();
          context.fill();

          // 添加行数
          context.fillStyle = "black";
          context.font = "14px Arial";
          const text = "Row " + (row + 1);
          const textWidth = context.measureText(text).width;
          context.fillText(text, arrowX1 - textWidth / 2, arrowY1 - 10);
        }

        col++;
        if (col >= numCols) {
          col = 0;
          row++;
        }

        frameCount++;

        if (frameCount < numRows * numCols) {
          setTimeout(drawFrame, 500);
        }
      };

      // 启动动画
      drawFrame();
    },
    getRandomColor() {
      const letters = "0123456789ABCDEF";
      let color = "#";
      for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
      }
      return color;
    },
  },
};
</script>

<style>
/* 在这里添加样式 */
</style>
