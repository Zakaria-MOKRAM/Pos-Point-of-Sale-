import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  TbBackspace,
  TbNumber0,
  TbNumber1,
  TbNumber2,
  TbNumber3,
  TbNumber4,
  TbNumber5,
  TbNumber6,
  TbNumber7,
  TbNumber8,
  TbNumber9,
  TbLogin2,
} from "react-icons/tb";

import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

import { instance as axios } from "@/app/axios";
import Cookies from "js-cookie";

import { useSetAtom } from "jotai";
import { authStateAtom, authTokenAtom, authenticatedUserAtom } from "@/app/store/atoms/auth";
import { useToast } from "@/components/ui/use-toast";
import { useTranslation } from "react-i18next";

export default function Auth() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [users, setUsers] = useState([]);

  const navigate = useNavigate();

  const { t } = useTranslation();
  const { toast } = useToast();

  const setAuthState = useSetAtom(authStateAtom);
  const setAuthToken = useSetAtom(authTokenAtom);
  const setAuthenticatedUser = useSetAtom(authenticatedUserAtom);

  const fetchUsers = async () => {
    console.log('hello world :',  import.meta.env.VITE_SERVER_URL)
    const api = `${import.meta.env.VITE_SERVER_URL}/auth/users`;
    const response = await fetch(api);
    const fetchedData = await response.json();
    setUsers(fetchedData);
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  const handleBackspace = () => setPassword(password.slice(0, -1));
  const handleNumberClick = (digit) => setPassword((prevPin) => prevPin + digit);
  const handleLogin = async () => {
    await axios
      .post("/auth/sign-in", { username, password })
      .then((response) => {
        const token = response.data.token;

        if (token) {
          Cookies.set("authToken", token);

          setAuthState(true);
          setAuthToken(token);
          setAuthenticatedUser({
            id: response.data.id,
            firstname: response.data.firstname,
            lastname: response.data.lastname,
            username: response.data.username,
            role: response.data.role,
          });

          navigate("/");
        }
      })
      .catch(() => {
        toast({
          variant: "destructive",
          title: "Uh oh! Something went wrong.",
          description: "There was a problem with your credentials.",
        });
      });
  };

  const selectLabelBuilder = (user) => {
    return user.firstname !== null && user.lastname !== null
      ? `${user.firstname} ${user.lastname}`
      : `@${user.username}`;
  };

  return (
    <div className="h-screen flex flex-col justify-center items-center ">
      <div className="flex flex-col gap-6">
        {/* <img src={brandImage} className="w-20 m-auto rounded-full shadow shadow-stone-300" /> */}

        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-2">
            <Select id="username" onValueChange={(value) => setUsername(value)}>
              <SelectTrigger className="w-[14.5rem] shadow-none">
                <SelectValue placeholder={t("auth.selectUsername")} />
              </SelectTrigger>
              <SelectContent className="mb-2">
                <SelectGroup>
                  {users &&
                    users.length > 0 &&
                    users.map((user) => (
                      <SelectItem key={user.username} value={user.username}>
                        {selectLabelBuilder(user)}
                      </SelectItem>
                    ))}
                </SelectGroup>
              </SelectContent>
            </Select>

            <Input id="password" className="w-[14.5rem]" value={password || ""} type="password" readOnly />
          </div>

          <div className="flex gap-2">
            <div className="flex flex-col gap-2">
              <NumberButton icon={TbNumber1} onClick={() => handleNumberClick("1")} />
              <NumberButton icon={TbNumber4} onClick={() => handleNumberClick("4")} />
              <NumberButton icon={TbNumber7} onClick={() => handleNumberClick("7")} />
              <DeleteButton icon={TbBackspace} onClick={handleBackspace} />
            </div>
            <div className="flex flex-col gap-2">
              <NumberButton icon={TbNumber2} onClick={() => handleNumberClick("2")} />
              <NumberButton icon={TbNumber5} onClick={() => handleNumberClick("5")} />
              <NumberButton icon={TbNumber8} onClick={() => handleNumberClick("8")} />
              <NumberButton icon={TbNumber0} onClick={() => handleNumberClick("0")} />
            </div>
            <div className="flex flex-col gap-2 mb-2">
              <NumberButton icon={TbNumber3} onClick={() => handleNumberClick("3")} />
              <NumberButton icon={TbNumber6} onClick={() => handleNumberClick("6")} />
              <NumberButton icon={TbNumber9} onClick={() => handleNumberClick("9")} />
              <LoginButton icon={TbLogin2} onClick={handleLogin} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export const DeleteButton = ({ onClick, icon: Icon }) => (
  <Button className="h-14 w-[4.5rem] text-xl shadow shadow-stone-300" onClick={onClick} variant="outline">
    {Icon ? <Icon /> : ""}
  </Button>
);

export const NumberButton = ({ onClick, icon: Icon }) => (
  <Button className="h-14 w-[4.5rem] text-xl shadow shadow-stone-300" onClick={onClick} variant="outline">
    {Icon ? <Icon /> : ""}
  </Button>
);

export const LoginButton = ({ onClick, icon: Icon }) => (
  <Button className="h-14 w-[4.5rem] text-xl shadow shadow-stone-300 mb-2" onClick={onClick}>
    {Icon ? <Icon /> : ""}
  </Button>
);
