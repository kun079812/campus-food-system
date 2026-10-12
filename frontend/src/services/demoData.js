// Explicit preview data is kept separate from the real Spring Boot session.
export const DEMO_KEY = 'canteen-preview-db-v1'
export function createDemoData() {
  const today = new Date().toISOString()
  const dishes = [
    {
      id: 1,
      name: '红烧肉盖饭',
      price: 12.8,
      categoryId: 1,
      description: '慢火炖煮，软糯入味。一口是熟悉的家常滋味。',
      stock: 38
    },
    {
      id: 2,
      name: '宫保鸡丁饭',
      price: 11.8,
      categoryId: 1,
      description: '鲜嫩鸡丁搭配香脆花生，微辣开胃，元气满满。',
      stock: 46
    },
    {
      id: 3,
      name: '香辣牛肉面',
      price: 13.8,
      categoryId: 2,
      description: '醇厚汤底，大块牛肉，现煮面条暖胃又暖心。',
      stock: 26
    },
    {
      id: 4,
      name: '番茄鸡蛋饭',
      price: 11.8,
      categoryId: 1,
      description: '酸甜番茄遇见金黄鸡蛋，简单却总是吃不腻。',
      stock: 42
    },
    {
      id: 5,
      name: '酱香排骨饭',
      price: 12.8,
      categoryId: 1,
      description: '浓郁酱香裹着软嫩排骨，配上热腾腾的米饭。',
      stock: 23
    },
    {
      id: 6,
      name: '鲜柠檬茶',
      price: 6,
      categoryId: 3,
      description: '鲜切柠檬与清香茶底，给忙碌的校园生活一点清爽。',
      stock: 60
    }
  ].map((dish) => ({ ...dish, stallId: 1, status: 1, imageUrl: '' }))
  const orders = ['WAIT_ACCEPT', 'READY_PICKUP', 'COMPLETED'].map((status, index) => ({
    id: 2458 - index,
    orderNo: `DEMO2026100${2458 - index}`,
    userId: 9001,
    stallId: 1,
    totalAmount: dishes[index].price,
    payAmount: dishes[index].price,
    status,
    deliveryType: 'PICKUP',
    createdAt: today,
    remark: '',
    orderItems: [
      {
        dishId: dishes[index].id,
        dishName: dishes[index].name,
        quantity: 1,
        price: dishes[index].price
      }
    ],
    statusLogs: [{ oldStatus: 'CREATE', newStatus: status, createdAt: today }]
  }))
  return {
    dishes,
    orders,
    cart: [],
    reviews: [],
    categories: [
      { id: 4, stallId: 1, name: '早餐' },
      { id: 5, stallId: 1, name: '午餐' },
      { id: 6, stallId: 1, name: '晚餐' },
      { id: 7, stallId: 1, name: '特色窗口' },
      { id: 2, stallId: 1, name: '面食' },
      { id: 1, stallId: 1, name: '米饭' },
      { id: 8, stallId: 1, name: '小吃' },
      { id: 3, stallId: 1, name: '饮品' }
    ],
    addresses: [
      {
        id: 1,
        contactName: '林同学',
        phone: '13800000000',
        detail: '校园东区 3 号楼一层',
        isDefault: 1,
        status: 1
      }
    ],
    stall: {
      id: 1,
      canteenId: 1,
      merchantId: 9002,
      name: '第一食堂 · 暖心小厨',
      businessHours: '07:00 - 21:00',
      status: 1
    }
  }
}
export function readDemo() {
  try {
    const value = JSON.parse(localStorage.getItem(DEMO_KEY))
    if (value?.dishes?.length && Array.isArray(value.orders)) return value
  } catch {
    /* Start with clean sample data if local storage was edited. */
  }
  return createDemoData()
}
