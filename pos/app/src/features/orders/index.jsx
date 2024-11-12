import React, { useEffect } from "react";
import { useSetAtom } from "jotai";

import useSWR from "swr";
import { fetcher } from "@/app/axios";

import { agentsAtom, chosenAgentAtom, orderAtom } from "@/app/store/atoms/orders";
import { categoriesAtom } from "@/app/store/atoms/categories";
import { selectedTableAtom } from "@/app/store/atoms/tables";
import { recipesAtom } from "@/app/store/atoms/recipes";

import { Loader } from "@/components/composite/spinners";
import { MainHeader } from "@/components/composite/headers";
import { OrderSummary, Keypad, MenuLayout } from "@/features/orders/components";
import { ProtectedRoute } from "@/components/composite/protected-route";
import { useTranslation } from "react-i18next";

export default function Orders() {
  const { t } = useTranslation();

  const headerLinks = [
    { id: 1, label: t("orders.menu.foodiesMenu"), path: "/orders" },
    { id: 2, label: t("orders.order.orderLine"), path: "/summaries" },
  ];

  const { data: categories, isLoading: categoriesLoading } = useSWR("/categories", fetcher);
  const { data: recipes, isLoading: recipesLoading } = useSWR("/items", fetcher);
  const { data: agents, isLoading: agentsLoading } = useSWR("/agents", fetcher);

  const addAgents = useSetAtom(agentsAtom);
  const addRecipes = useSetAtom(recipesAtom);
  const addCategories = useSetAtom(categoriesAtom);

  const updateTable = useSetAtom(selectedTableAtom);
  const updateOrder = useSetAtom(orderAtom);
  const updateAgent = useSetAtom(chosenAgentAtom);

  const handleEmptySelectedTable = () => {
    updateTable(null);
    updateAgent(null);
    updateOrder({ id: null, items: [], subTotal: 0, discount: 0, total: 0 });
  };

  useEffect(() => {
    !recipesLoading && recipes ? addRecipes([...recipes]) : addRecipes([]);
  }, [recipesLoading]);

  useEffect(() => {
    !categoriesLoading && categories ? addCategories([...categories]) : addCategories([]);
  }, [categoriesLoading]);

  useEffect(() => {
    !agentsLoading && agents ? addAgents([...agents]) : addAgents([]);
  }, [agentsLoading]);

  return (
    <ProtectedRoute>
      {!categoriesLoading && !recipesLoading ? (
        <div className="min-h-screen">
          <MainHeader prevButtonIncluded links={headerLinks} action={handleEmptySelectedTable} />
          <div className="h-svh flex px-8">
            <div className="w-full flex flex-col">{!recipesLoading && <MenuLayout />}</div>
            <div className="h-full min-w-[24rem] max-w-[24rem] flex flex-col border-s bg-white border-neutral-200 ps-4">
              <OrderSummary />
              <Keypad />
            </div>
          </div>
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center">
          <Loader />
        </div>
      )}
    </ProtectedRoute>
  );
}
