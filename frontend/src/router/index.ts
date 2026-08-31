import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth.ts";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("../views/LoginView.vue"),
      meta: { public: true },
    },
    {
      path: "/",
      name: "transactions",
      component: () => import("../views/TransactionsView.vue"),
    },
    {
      path: "/:pathMatch(.*)*",
      redirect: "/",
    },
  ],
});
router.beforeEach((to) => {
  const auth = useAuthStore();

  if (to.meta.public) {
    return true;
  }

  if (!auth.isAuthenticated) {
    return { name: "login" };
  }

  return true;
});
export default router;
